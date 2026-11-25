## Gratitude Journal — Project Analysis (2026-11-25)

This document summarizes the current architecture, dependencies, build setup, and key flows of the app to support upgrades and new features.

### Tech Stack
- **Language**: Kotlin `2.4.21` (AGP Built-in Kotlin, JVM target `17`)
- **UI**: Jetpack Compose (Compose BOM `2026.09.00`, Material3, Navigation `2.10.2`)
- **DI**: Hilt `2.60.1` (via KSP `2.3.14`)
- **Networking**: Ktor client `3.6.2` (CIO), Kotlinx Serialization
- **Auth**: Google Sign-In (`play-services-auth:21.6.0`), Firebase Auth (via Firebase BOM `35.2.0`)
- **Storage**: Firebase Firestore (via Firebase BOM `35.2.0`), Room `2.8.5` (via KSP `2.3.14`; schemas tracked under `app/schemas`)
- **Images/GIFs**: Coil 3 (`3.6.3`: `coil-compose`, `coil-gif`, `coil-network-ktor3`), GIPHY SDK UI `2.5.3`
- **Logging**: Timber `5.0.1`
- **Coroutines**: Kotlinx Coroutines `1.11.0`

### Modules and Structure
- Single module app: `:app`
- App namespace: `tech.svehla.gratitudejournal`
- Entry point: `AndroidManifest.xml` declares `MainActivity` and `application` `core.GratitudeJournalApp`
- Compose navigation with bottom bar: `core/presentation/MainSkelet.kt`

Key packages under `app/src/main/java/tech/svehla/gratitudejournal`:
- `core/di` — Hilt modules (e.g., `NetworkModule.kt`)
- `core/data/remote` — API services, Ktor client, Firestore implementation, DTOs, error handling, `Resource` wrapper
- `core/domain` — Domain models
- `core/presentation` — Navigation, delegates, common UI components/theme
- Feature packages:
  - `history` (screen + viewmodel + domain use case)
  - `detail` (screen + viewmodel + domain use cases)
  - `settings` (screen + viewmodel + auth use cases)

### Build Configuration
- Gradle wrapper `9.9.2`, AGP `9.5.3` (Built-in Kotlin `2.4.21`), KSP `2.3.14` (replaced `kapt`), Java `17` toolchain target
- `compileSdk 37`, `targetSdk 37`, `minSdk 26`
- Compose: `org.jetbrains.kotlin.plugin.compose` (`2.4.21`), managed via Compose BOM `2026.09.00`
- Debug build type only enabled; release block currently commented out
- `buildConfigField`s in `defaultConfig`:
  - `WEB_CLIENT_ID` (Google Sign-In Web client ID) from `local.properties`
  - `GIPHY_API_KEY` from `local.properties`
- Signing configs: debug provided; a placeholder release config exists (and an optional external keystore loader)
- Plugins (`plugins {}` DSL): Android application (`9.5.3`), KSP (`2.3.14`), Kotlin Serialization (`2.4.21`), Hilt (`2.60.1`), Google Services (`4.6.0`), Kotlin Compose (`2.4.21`), Dependency Analysis (`3.9.0`, root)
- Versions managed in `gradle/libs.versions.toml`

### Dependencies (high-level)
- Compose UI (BOM `2026.09.00`): `ui`, `material3`, `ui-tooling-preview` (debug); `navigation-compose` (`2.10.2`), `activity-compose` (`1.13.0`)
- AndroidX & Lifecycle: `core-ktx` (`1.19.1`), `activity-ktx` (`1.13.0`), `lifecycle-runtime-compose` (`2.11.1`)
- Google services & Firebase (BOM `35.2.0`): `play-services-auth` (pinned at `21.6.0` because `22.0.0` removes legacy `GoogleSignIn`), `play-services-auth-api-phone` (`18.3.1`), `firebase-auth`, `firebase-firestore` (migrated off `-ktx` artifacts)
- Room (`2.8.5`): runtime, ktx, compiler via KSP (schemas exported to `app/schemas`)
- Ktor client (`3.6.2`): `core`, `cio`, `content-negotiation`, `logging`, `serialization-kotlinx-json`
- Hilt (`2.60.1`): `hilt-android`, compiler via KSP, `hilt-navigation-compose` (`1.4.0`)
- Media: `giphy` UI (`2.5.3`), Coil 3 (`3.6.3`: `coil-compose`, `coil-gif`, `coil-network-ktor3`)
- Testing: JUnit4 (`4.13.2`), Turbine (`1.2.1`), Truth (`1.4.5`), Coroutines Test (`1.11.0`)

### Application and Navigation
- `MainSkelet.kt` defines:
  - Bottom nav with routes: `History`, `Detail`, `Settings`
  - `NavHost` start destination: `History`
  - `Detail` requires a `date` argument; helper `constructRoute(date)`

### State Management and ViewModels
- Hilt ViewModels:
  - `HistoryViewModel` consumes `GetHistoryUseCase` and exposes `HistoryScreenState`. Collects `Resource`-wrapped flow.
  - `DetailViewModel` uses `GetDetailUseCase` and `SaveEntryUseCase`. Loads data by `date`, tracks initial vs. edited state; on `onStop`, saves if modified using an injected `@ApplicationScope` `CoroutineScope`.
  - `SettingsViewModel` handles Google sign-in flow via `SignInWithGoogleUseCase`, `SignOutUseCase`, and exposes `SettingsScreenState`. Loads current user via `CurrentUserUseCase`.

### Networking and Data Layer
- Hilt `NetworkModule` provides:
  - Singleton `HttpClient` from `core.data.remote.util.ktorHttpClient`
  - `ApiService` bound to `FirestoreServiceImpl` by default (Ktor variant present but disabled)
  - `AuthService` bound to `AuthServiceImpl`
- `KtorClient.kt` config:
  - Engine: CIO, `requestTimeout = 10_000ms`
  - `ContentNegotiation` with configured `Json` (pretty print, lenient)
  - `Logging` with `Logger.DEFAULT`, `LogLevel.HEADERS`, with header sanitization; filter only logs hosts containing `ktor.io`
- `KtorApiServiceImpl` (present but not currently wired):
  - `BASE_URL` hardcoded to `http://192.168.68.62:8080`
  - Endpoints: `GET /entries`, `GET /entries/{date}`, `POST /entries`
  - Adds `Authorization: Bearer {AuthService.currentToken}` header
- Firestore path implied via `FirestoreServiceImpl` (implementation exists in project, injected as `ApiService`)

### Authentication
- Google Sign-In via `play-services-auth`
- Firebase Auth present; `AuthServiceImpl` manages `currentToken` used by Ktor API when enabled
- `AuthDelegate` exists but `AuthDelegateImpl` is a stub (TODO methods not implemented). Actual sign-in UX handled in `SettingsViewModel`.

### Room and Persistence
- Room dependencies and compiler enabled with schema export to `app/schemas` (versions and prior namespace migrations present)
- Local DB likely used for caching or offline-first; requires further scan of DAO/entities to confirm exact usage (not included in this short pass)

### Resources and Theming
- Material3 theme under `core/presentation/ui/theme`
- Various UI components: `ErrorScreen`, `LoadingScreen`, `NumberPicker`, `GiphyPicker`, `SignInWithGoogleButton`

### Notable Flags and Defaults
- Manifest sets `android:usesCleartextTraffic="true"` (allows HTTP)
- Internet permission present
- Debug build defines `BuildConfig.DEV_MODE = true`
- Ktor logs filtered narrowly (may hide useful logs for non-ktor.io hosts)

### Current Risks and Gaps
- `ApiService` uses `FirestoreServiceImpl` by default; `KtorApiServiceImpl` is present but not active. The hardcoded base URL is a LAN IP; not suitable for production.
- `AuthDelegateImpl` methods are not implemented; ensure it’s not used or implement it before exposing related UI.
- Release signing is partially configured with placeholder secrets; ensure secure keystore handling or the alternate `certificates/keystore.properties` path.
- Cleartext traffic allowed; if continuing HTTP, consider scoping via network security config instead of broad `usesCleartextTraffic`.
- Compose compiler extension is pinned to `1.5.1` while Compose libs are `1.7.x`. Validate compatibility.
- Logging filter limits Ktor logs to hosts containing `ktor.io` (likely unintended in this app).

### Recommendations for Upcoming Upgrades
1. Networking
   - Decide primary backend: Firestore vs. custom Ktor API. Remove dead code or add build flavor(s) to switch.
   - Externalize base URL and auth header injection; use `BuildConfig` fields and `DefaultRequest` plugin.
   - Adjust Ktor logging filter to include app hosts or remove filter in debug builds.
   - Add structured error mapping in a single place (consider a `ErrorHandler` if not already centralised).

2. Auth
   - Ensure `AuthDelegateImpl` is either implemented or removed. Current auth handled in `SettingsViewModel` looks clean.
   - Verify `WEB_CLIENT_ID` in `local.properties`; add validation at app start for missing keys.

3. Build and CI
   - Reinstate release build type with ProGuard/R8 if targeting Play release.
   - Move placeholder release signing out; rely on `certificates/keystore.properties` only.
   - Add CI checks: detekt/ktlint, unit tests, assemble, dependency analysis.

4. Room and Offline
   - If Room is in use: document entity/DAO relationships and sync policies; consider migration tests.
   - If unused: remove to simplify footprint.

5. UX/Navigation
   - Consider top app bar reinstatement with dynamic titles.
   - `Detail` save behavior on `onStop` is good; add explicit save action and feedback.

6. Secrets and Config
   - Ensure `local.properties` keys (`webClientId`, `giphyApiKey`) are present in dev env; add README section.
   - Consider using `gradle.properties`/`dotenv`-like approach per build type.

### Quick Reference: Key Files
- Build
  - `settings.gradle`: includes `:app`, plugin management
  - `build.gradle.kts` (root): AGP, Kotlin, Hilt, Google Services classpaths
  - `app/build.gradle.kts`: app config, Compose, Ktor, Firebase, Hilt, Room
- App
  - `AndroidManifest.xml`: `MainActivity`, `core.GratitudeJournalApp`, cleartext enabled
  - `core/di/NetworkModule.kt`: HttpClient, ApiService, AuthService providers
  - `core/data/remote/util/KtorClient.kt`: Ktor client
  - `core/data/remote/implementation/KtorApiServiceImpl.kt`: Ktor endpoints (LAN base URL)
  - `core/presentation/MainSkelet.kt`: navigation & bottom bar
  - `history/detail/settings` viewmodels and screens

This document will be kept up-to-date as we refactor and add features.
