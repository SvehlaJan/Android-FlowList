# Long-term Upgrade and KMP Migration Plan

## Goals
- Modernize build toolchain and libraries to current stable.
- Remove legacy/config risks (cleartext, hardcoded base URL).
- Restructure for Kotlin Multiplatform migration with minimal churn.

## Phase 1 — Baseline modernization (Android-only)
- Toolchain
  - Upgrade Gradle wrapper and Android Gradle Plugin.
  - Move project to Java 17 toolchain; set compileOptions/kotlinOptions accordingly.
  - Upgrade Kotlin to latest stable; align Compose compiler with Kotlin version.
- Compose and lifecycle
  - Switch to Compose BOM to manage versions; update material3, navigation-compose, activity-compose, lifecycle-runtime-compose.
  - Re-verify composeOptions.kotlinCompilerExtensionVersion matches the Kotlin/Compose combo.
- Coroutines and serialization
  - Bump kotlinx-coroutines and kotlinx-serialization to latest stable; ensure Ktor JSON matches.
- DI and logging
  - Update Hilt and hilt-navigation-compose.
  - Keep Timber at latest.
- Networking
  - Upgrade Ktor client to latest stable; adopt DefaultRequest for headers and base URL.
  - Move base URL to BuildConfig per build type; remove hardcoded LAN IP.
  - Adjust Ktor logging filter to include your host (or log all in debug).
- Data
  - Upgrade Room to latest; if not actively used, either remove or document its scope.
- Firebase/Google
  - Migrate to Firebase BOM; update firebase-auth-ktx and firebase-firestore-ktx.
  - Update Play Services Auth.
- Images/GIF
  - Update Coil; review GIPHY SDK status.
- Config/security
  - Remove global usesCleartextTraffic; add a debug-only Network Security Config if HTTP is needed, otherwise enforce HTTPS.
  - Reinstate release buildType with R8 and secure signing.

## Phase 2 — KMP-ready refactor (within Android project)
- Layering
  - Define domain/data/app boundaries; keep domain/data pure Kotlin.
- DI
  - Keep Hilt in Android app; design constructors for domain/data so they are DI-agnostic.
- Networking/storage abstractions
  - Interfaces for ApiService, AuthService, JournalRepository behind pure Kotlin contracts.
- Firebase
  - Wrap Firebase calls behind interfaces for swapability in KMP.
- Local database
  - Decide: Room KMP vs SQLDelight; abstract persistence now.

**See [KMP_MIGRATION_STRATEGY.md](./KMP_MIGRATION_STRATEGY.md) for detailed migration analysis and library compatibility matrix.**

## Phase 3 — Introduce a KMP shared module
- Create shared module with commonMain, androidMain, iosMain.
- Use Ktor, Kotlinx Serialization, Coroutines (multiplatform).
- DI in shared via Koin or manual providers; Hilt stays on Android.
- Persistence via SQLDelight or Room KMP.
- Android integrates shared; later add iOS host.

## Phase 4 — Testing, CI, and rollout
- Shared unit tests; Android integration tests.
- CI: Java 17, AGP/Kotlin upgrades, lint, unit tests; optional iOS shared build.
- Release hardening: R8 rules, signing, privacy.

## Risks/Considerations
- Align Kotlin/Compose compiler/BOM versions.
- Firebase multiplatform support may require wrappers/alternatives.
- Cleartext restrictions for local dev via debug-only network security config.
