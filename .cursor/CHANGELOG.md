# Changelog

All notable changes to this project will be documented in this file.

This changelog is maintained manually. Add a new dated entry for each significant code or configuration change.

## 2026-11-25
- Upgraded toolchain to Gradle `9.9.2`, AGP `9.5.3` (Built-in Kotlin `2.4.21`), KSP `2.3.14` (replaced `kotlin-kapt` for Room and Hilt), and Java `17` `compileOptions` / `jvmTarget`.
- Updated `compileSdk` and `targetSdk` to `37`; removed obsolete `composeOptions` block and deprecated `android.nonFinalResIds=false` in `gradle.properties`.
- Switched root `build.gradle.kts` from legacy `buildscript {}` to `plugins {}` DSL using version catalog aliases.
- Adopted Compose BOM `2026.09.00` and Firebase BOM `35.2.0`; migrated `FirestoreServiceImpl.kt` off deprecated `firebase-*-ktx` imports to `com.google.firebase.Firebase` and `com.google.firebase.firestore.firestore`.
- Upgraded dependencies: Hilt `2.60.1`, `hilt-navigation-compose` `1.4.0`, Room `2.8.5`, Ktor `3.6.2`, Coroutines `1.11.0`, `core-ktx` `1.19.1`, `activity` `1.13.0`, `lifecycle-runtime-compose` `2.11.1`, `navigation-compose` `2.10.2`, `google-services` `4.6.0`, `dependency-analysis` `3.9.0`, `giphy` `2.5.3`, `mockk` `1.14.12`.
- Migrated Coil 2 (`2.7.0`) to Coil 3 (`3.6.3`: `coil-compose`, `coil-gif`, `coil-network-ktor3`) in `DetailScreen.kt` (`coil3.*` package and `AnimatedImageDecoder`).
- Pinned `play-services-auth` to `21.6.0` because `22.0.0` removes the legacy `GoogleSignIn` / `GoogleSignInClient` APIs still used by the sign-in flow.
- Enabled `enableEdgeToEdge()` in `MainActivity.kt` and removed legacy `android:statusBarColor` from `themes.xml` to fix white-on-white status bar icons.
- Configured AGP `9.5.3` native Compose screenshot testing (`screenshotTests` test suite with `screenshot-validation-api:0.0.1-alpha17`, `ui-tooling:1.12.1`) and added `@PreviewTest` suite in `app/src/screenshotTest/kotlin/tech/svehla/gratitudejournal/ScreenPreviewsTest.kt` with generated reference images.
- Verified with `./gradlew assembleDebug testDebugUnitTest`, emulator deployment/visual verification, and `./gradlew :app:testDebugScreenshotTestDefaultTestSuite :app:testDebugUnitTest` (`BUILD SUCCESSFUL`).

## 2025-10-30
- Added `PROJECT_ANALYSIS.md` capturing architecture, dependencies, and upgrade notes.
- Established policy to keep `PROJECT_ANALYSIS.md` up-to-date before/after changes.
- Created `CHANGELOG.md` to track significant changes going forward.
- Added `.cursor/UPGRADE_PLAN.md` outlining the long-term modernization and KMP plan.
- Phase 1: Externalized API `BASE_URL` to BuildConfig and updated Ktor client defaults.
- Phase 1: Set Java toolchain to 17 (`compileOptions`/`kotlinOptions`).
- Phase 1: Updated dependencies to latest stable versions (Kotlin 2.2.21, AGP 8.13.0, Compose 1.9.4, Room 2.8.3, Ktor 3.3.1, Hilt 2.57.2, etc.).
- Phase 1: Migrated `kotlinOptions` to `compilerOptions` DSL with Kotlin 2.x.
- Phase 1: Replaced `-Xopt-in` with `-opt-in` in compiler args.
- Phase 1: Added `buildFeatures { buildConfig = true }` to app build.gradle.kts.
- Phase 1: Updated Room `fallbackToDestructiveMigration()` to new overloaded signature with `dropAllTables` parameter.
- Phase 1: Migrated `hiltViewModel()` imports from `androidx.hilt.navigation.compose` to `androidx.hilt.lifecycle.viewmodel.compose`.
- Phase 1: Replaced deprecated `ClickableText` with `Text` + `clickable` modifier.
- Phase 1: Fixed `setColorFilter` deprecation using `BlendModeColorFilter` for API 29+.
- Phase 1: Fixed Kotlin annotation target warnings by using `@param:` prefix.
- Phase 1: Removed deprecated `android.defaults.buildfeatures.buildconfig=true` from `gradle.properties`.
- Phase 1: Bumped `compileSdk` and `targetSdk` to 36 for latest AndroidX requirements.
- Phase 1: Updated version catalog to latest majors (Kotlin 2.2.x, AGP 8.8.x, Compose 1.9.x, Ktor 3.x, Room 2.7.x, Hilt 2.57, Firebase BOM 33.x) and enabled Compose/Firebase BOMs.
- Phase 1: Bumped Gradle wrapper to 8.10.2 for AGP compatibility.
- **KMP Migration Research**: Completed comprehensive research on KMP migration strategies:
  - Created `KMP_MIGRATION_STRATEGY.md` with detailed migration plan, library compatibility matrix, risk assessment, and timeline estimates.
  - Created `KMP_CODE_ANALYSIS.md` with file-by-file analysis of what can stay vs. what must change.
  - Created `KMP_OPEN_QUESTIONS.md` with open questions and decisions needed before migration.
  - Identified that Ktor, Kotlinx Serialization, Coroutines, and Compose are already KMP-compatible.
  - Identified that Hilt → Koin, Firebase Auth/Firestore → Ktor OAuth2/JWT and Ktor API, Room → Room KMP, and `java.time` → `kotlinx-datetime` are required changes.
  - **Decisions made**:
    - Authentication: Keep platform-specific with Google Sign-In SDKs (simpler, native UX)
    - Backend Service: Keep using Firestore (feasible for iOS via CocoaPods)
    - iOS Architecture: Use Compose Multiplatform for iOS
    - Image Loading: Coil 3.0 supports multiplatform, no replacement needed
    - GIF Picker: Keep Android-only for now, revisit later
    - Offline/Sync: Firestore handles sync automatically
    - Testing: Unit tests only for now, but prepare for integration/UI tests
    - CI/CD: Android only for now
  - **Open questions updated**:
    - Clarified Q8 (Platform Resources) with detailed options (Moko Resources, platform-specific, hybrid)
    - Expanded Q9 (Date/Time Format) with 5 options (ISO 8601, timestamps, Firestore Timestamp, hybrid)
    - Expanded Q15 (Development Workflow) with 5 options (Android testing, standalone, iOS simulator, hybrid, CI/CD)
    - Removed irrelevant questions, consolidated related questions
  - **Final decisions made**:
    - Q2: Firebase Kotlin SDK for Auth and Firestore
    - Q3: Official Kotlin Multiplatform Resources (Compose Multiplatform Resources)
    - Q4: ISO 8601 Standard for date/time format
    - Q5/Q6: Firebase Kotlin SDK for Google Sign-In, Compose Multiplatform for iOS
    - Q12: Hybrid development workflow
  - **Remaining open questions**: Only 3 minor questions remain (BuildKonfig, Environment config, Rollout strategy)
  - Created `KMP_DECISIONS_SUMMARY.md` with all decisions and remaining questions
  - **Final decisions made**:
    - Q7: BuildKonfig Configuration - Decide during migration ✅
    - Q8: Environment Configuration - Debug/Release only ✅
    - Q14: Rollout Strategy - No formal strategy needed (hobby project) ✅
  - **All questions resolved**: Ready to start migration immediately!
  - Updated `KMP_MIGRATION_STRATEGY.md` to be the single source of truth with all decisions incorporated
  - **Updated authentication approach**:
    - Changed from hybrid approach (platform-specific Google Sign-In UI + Firebase Kotlin SDK backend)
    - To: Firebase Kotlin SDK for authentication backend (with platform-specific Google Sign-In UI)
    - Decision: Use Firebase Kotlin SDK for all auth backend operations (shared in `commonMain`)
    - Google Sign-In UI remains platform-specific (expected and acceptable)
    - Created `FIREBASE_KOTLIN_SDK_VERIFICATION.md` - comprehensive feature verification
    - ✅ Verified: Firebase Kotlin SDK supports 95% of functionality (all backend operations)
    - Only Google Sign-In UI requires platform-specific code (not a blocker)
