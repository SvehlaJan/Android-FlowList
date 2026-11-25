# Kotlin Multiplatform Migration Strategy

**Date**: 2025-10-30  
**Status**: ✅ Ready to Start Migration  
**Target Platforms**: Android, iOS

> **📌 Single Source of Truth**: This document contains all migration decisions and serves as the single point of reference for the KMP migration.

## Executive Summary

This document outlines the migration strategy for converting the Gratitude Journal Android app to Kotlin Multiplatform (KMP). The goal is to share business logic, data models, and potentially UI (via Compose Multiplatform) between Android and iOS platforms while maintaining platform-specific implementations where necessary.

**Project Context**: This is a hobby project that most likely won't be published, allowing for flexible migration approach without formal rollout strategy.

## ✅ Migration Decisions Summary

### Architecture & Technology Stack

1. **Authentication**: Firebase Kotlin SDK ✅
   - **Decision**: Use Firebase Kotlin SDK for all authentication operations
   - **Library**: [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
   - **Support**: Firebase Authentication backend operations (fully supported in `commonMain`)
   - **Implementation**: `implementation("dev.gitlive:firebase-auth:4.3.0")`
   - **Benefits**: 
     - Fully multiplatform authentication backend (works in `commonMain`)
     - Shared auth logic between Android and iOS
     - Type-safe with Kotlin Serialization
     - All auth backend operations (sign in, sign out, user tracking) in shared code
   - **Google Sign-In UI**: 
     - **Requires platform-specific implementation** (Google Sign-In SDKs for UI)
     - **Pattern**: Platform-specific Google Sign-In UI → Extract ID token → Firebase Kotlin SDK for auth
     - **Android**: Google Sign-In SDK (`com.google.android.gms:play-services-auth`)
     - **iOS**: Google Sign-In iOS SDK
   - **Verified**: ✅ Firebase Kotlin SDK supports all required auth features (see `FIREBASE_KOTLIN_SDK_VERIFICATION.md`)
   - **Pattern**: Platform Google Sign-In UI → Extract ID token → Firebase Kotlin SDK Auth → Firestore access

2. **Backend Service**: Firebase Kotlin SDK for Firestore ✅
   - **Library**: [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
   - **Support**: All Firestore operations (fully supported)
   - **Benefits**: Fully multiplatform, shared code in `commonMain`, type-safe with Kotlin Serialization
   - **Verified**: ✅ Firebase Kotlin SDK supports all required Firestore features (see `FIREBASE_KOTLIN_SDK_VERIFICATION.md`)
   - **Implementation**: `implementation("dev.gitlive:firebase-firestore:4.3.0")`
   - **Features Supported**:
     - Collection queries ✅
     - Document queries ✅
     - Document writes ✅
     - Kotlin Serialization ✅
     - Real-time listeners (Flow-based) ✅
     - Offline persistence ✅

3. **Resources**: Official Kotlin Multiplatform Resources ✅
   - **Library**: [Compose Multiplatform Resources](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-setup.html) (Official JetBrains)
   - **Fallback**: Moko Resources if official doesn't meet needs
   - **Setup**: Resources in `commonMain/composeResources/` directory
   - **Dependency**: `compose.components.resources`

4. **Date/Time Format**: ISO 8601 Standard ✅
   - **Format**: `YYYY-MM-DD` (dates) or `YYYY-MM-DDTHH:mm:ssZ` (with timezone)
   - **Storage**: ISO 8601 strings in Firestore
   - **Internal**: `kotlinx-datetime` `LocalDate` / `Instant`
   - **Reason**: Standard, widely supported, human-readable, timezone-aware

5. **iOS Architecture**: Compose Multiplatform ✅
   - Shared UI code between Android and iOS
   - Maximum code sharing

6. **Image Loading**: Coil 3.0 ✅
   - Supports multiplatform (Android, iOS, JVM, JS, WASM)
   - Update to `coil-compose` and `coil-network-ktor` for KMP
   - No replacement needed

7. **GIF Picker**: Android-only for now ✅
   - Keep `GiphyPicker.kt` in `androidMain` source set
   - Revisit later for iOS implementation

### Development & Configuration

8. **Testing Strategy**: Unit tests only for now ✅
   - Run in `commonTest` on all platforms (JVM, Android, iOS simulator)
   - Use `kotlin.test` framework and MockK for KMP
   - Prepare for integration/UI tests later

9. **CI/CD**: Android only for now ✅
   - JVM unit tests, APK artifacts (debug and release)
   - Add iOS builds when iOS app is ready

10. **Development Workflow**: Hybrid approach ✅
    - Primary: JVM unit tests + Android app testing
    - Secondary: iOS testing when needed
    - CI/CD: Automated test runs

11. **Build Configuration**: BuildKonfig Plugin ✅
    - **Decision**: Decide which constants to expose during migration
    - **Likely includes**: Date format constants (ISO 8601), API keys (GIPHY), feature flags, version info
    - **Library**: [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) plugin

12. **Environment Configuration**: Debug and Release only ✅
    - **Decision**: Debug and Release only (sufficient for hobby project)
    - **Android**: Debug and Release build variants with different `google-services.json`
    - **iOS**: Debug and Release configurations with different `GoogleService-Info.plist`
    - **BuildKonfig**: Environment-specific constants in shared module

13. **Rollout Strategy**: No formal strategy required ✅
    - **Decision**: No formal rollout strategy (hobby project, won't be published)
    - **Approach**: Flexible migration at own pace
    - Can migrate feature by feature or all at once, depending on what works best

14. **Offline/Sync**: Firestore handles automatically ✅
    - Built-in offline persistence and real-time sync
    - Conflict resolution via server timestamps

## Migration Approach

### Strategy: Bottom-Up Migration

We'll use a **bottom-up approach**, starting with the core business logic and data layer, then working up to presentation:

1. **Phase 1**: Create shared module and migrate pure Kotlin code
2. **Phase 2**: Migrate networking layer (Ktor is already KMP-ready)
3. **Phase 3**: Migrate data persistence (Room → Room KMP)
4. **Phase 4**: Migrate DI (Hilt → Koin)
5. **Phase 5**: Migrate ViewModels and state management
6. **Phase 6**: Migrate UI (Compose Multiplatform)
7. **Phase 7**: Handle platform-specific implementations

### Alternative: Hybrid Approach

If time-to-market is critical, consider:
- Keep Android app as-is
- Create new iOS app that consumes shared KMP module
- Gradually migrate Android features to shared module

## Code Analysis: What Can Stay vs. What Must Change

### ✅ **Can Stay (Already KMP-Compatible)**

#### Libraries
- **Ktor Client** ✅
  - Fully KMP-compatible
  - Current implementation using `CIO` engine works on all platforms
  - Only need platform-specific engines: `androidMain` → `Android`, `iosMain` → `Darwin`

- **Kotlinx Serialization** ✅
  - Fully KMP-compatible
  - Current `@Serializable` annotations work on all platforms
  - DTOs can move directly to `commonMain`

- **Kotlinx Coroutines** ✅
  - Fully KMP-compatible
  - Current `Flow`, `StateFlow`, `suspend` functions work everywhere

- **Jetpack Compose** ✅ (via Compose Multiplatform)
  - Compose Multiplatform supports Android, iOS, Desktop, Web
  - UI code can be shared with minimal changes
  - Navigation Compose also has KMP support

#### Pure Kotlin Code
- **Domain Models** ✅
  - `JournalEntry`, `User` (data classes)
  - Move directly to `commonMain`
  - ⚠️ **Exception**: Replace `java.time.LocalDate` with `kotlinx-datetime`

- **Use Cases** ✅
  - `GetHistoryUseCase`, `GetDetailUseCase`, `SaveEntryUseCase`, `SignInWithGoogleUseCase`, `SignOutUseCase`, `CurrentUserUseCase`
  - Pure Kotlin, no platform dependencies
  - Move directly to `commonMain`

- **Repository Interfaces & Implementations** ✅
  - `MainRepository`, `MainRepositoryImpl`
  - Pure Kotlin logic, only depends on injected services
  - Move to `commonMain` after DI migration

- **Error Handling** ✅
  - `ErrorHandler`, `ErrorReason`, `Resource` wrapper
  - Pure Kotlin, move to `commonMain`

### ⚠️ **Needs Migration (Platform-Specific)**

#### Dependency Injection
- **Hilt** ❌ → **Koin** ✅
  - **Reason**: Hilt is Android-only, no KMP support
  - **Migration**: Replace all Hilt annotations with Koin
  - **Files affected**:
    - `core/di/NetworkModule.kt`
    - `core/di/PersistenceModule.kt`
    - `core/di/RepositoryModule.kt`
    - `core/di/ViewModelModule.kt`
    - All `@HiltViewModel` classes
  - **Pattern**: Use Koin's `module { }` DSL in `commonMain` with `expect`/`actual` for platform-specific providers

#### Data Persistence
- **Room** ⚠️ → **Room KMP** ✅
  - **Status**: Room 2.8.0+ has experimental KMP support
  - **Migration**: Use `expect`/`actual` for database builder
  - **Pattern**:
    ```kotlin
    // commonMain
    expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
    
    // androidMain
    actual fun getDatabaseBuilder() = Room.databaseBuilder(...)
    
    // iosMain
    actual fun getDatabaseBuilder() = Room.databaseBuilder(/* SQLite driver */)
    ```
  - **DAO Rules**: All DAO functions must be `suspend` or return `Flow` (no blocking calls)

#### Date/Time
- **java.time.LocalDate** ❌ → **kotlinx-datetime** ✅
  - **Reason**: `java.time` is JVM-only
  - **Files affected**:
    - `core/domain/model/JournalEntry.kt`
    - `core/data/local/entity/JournalEntryEntity.kt`
    - `core/presentation/MainSkelet.kt`
  - **Migration**: Replace `LocalDate` with `LocalDate` from `kotlinx-datetime`

#### Authentication
- **Firebase Auth** ⚠️ → **Firebase Kotlin SDK** ✅
  - **Decision**: Use Firebase Kotlin SDK for all authentication operations
  - **Library**: `dev.gitlive:firebase-auth`
  - **Support**: Firebase Authentication including Google Sign-In
  - **Benefits**: 
    - Fully multiplatform (works in `commonMain`)
    - Shared auth logic between Android and iOS
    - Single SDK for all auth operations
    - No need for platform-specific Google Sign-In SDKs
  - **Implementation**:
    - Use Firebase Kotlin SDK's Google Sign-In provider if supported
    - Platform-specific setup may be needed for OAuth client IDs/config
    - All auth operations (sign in, sign out, current user, token refresh) in `commonMain`
  - **Pattern**: 
    - Firebase Kotlin SDK (commonMain) → Authenticate (including Google Sign-In) → Firestore access
  - **Note**: Verify Firebase Kotlin SDK fully supports Google Sign-In on both platforms before finalizing

#### Backend Service
- **Firebase Firestore** ⚠️ → **Firebase Kotlin SDK** ✅
  - **Decision**: Use [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk) for Firestore
  - **Library**: `dev.gitlive:firebase-firestore`
  - **Benefits**: 
    - Fully multiplatform (works in `commonMain`)
    - Single SDK for both Android and iOS
    - Type-safe with Kotlin Serialization
    - Shared code, no platform-specific implementations needed
  - **Note**: Verify all required Firebase features are supported before finalizing

#### Logging
- **Timber** ❌ → **Platform-Specific Logging** ⚠️
  - **Pattern**: Use `expect`/`actual` for logging
  ```kotlin
  // commonMain
  expect fun logDebug(tag: String, message: String)
  expect fun logError(tag: String, message: String, throwable: Throwable?)
  
  // androidMain
  actual fun logDebug(tag: String, message: String) = Timber.d(message)
  
  // iosMain
  actual fun logDebug(tag: String, message: String) = NSLog("[$tag] $message")
  ```

#### Image Loading
- **Coil** ✅ → **Coil 3.0 for Multiplatform** ✅
  - **Status**: Coil 3.0 has multiplatform support (Android, iOS, JVM, JS, WASM)
  - **Migration**: Update to `coil-compose` and `coil-network-ktor` for KMP
  - **No changes needed**: Current Coil usage can move to `commonMain` with Coil 3.0
  - **Note**: Ensure using `coil-compose` artifact which supports Compose Multiplatform

#### GIF Picker
- **GIPHY SDK** ❌ → **Android-Only (Deferred)** ⚠️
  - **Decision**: Keep GIF picker Android-only for now, revisit later
  - **Current**: Uses Android GIPHY SDK UI component (`GiphyPicker.kt`)
  - **Migration**: 
    - Keep `GiphyPicker.kt` in `androidMain` source set
    - Conditionally show/hide on iOS (not implemented initially)
  - **Future**: May implement Compose Multiplatform picker or GIPHY API integration later

#### Build Configuration
- **BuildConfig** ❌ → **BuildKonfig Plugin** ✅
  - **Reason**: `BuildConfig` is Android-only
  - **Decision**: Decide which constants to expose during migration
  - **Library**: Use [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) plugin
  - **Likely Constants**: 
    - Date format constants (ISO 8601)
    - API keys (GIPHY API key for Android GIF picker)
    - Feature flags (e.g., enable GIF picker)
    - Version info (app version, build number)
  - **Note**: Firebase config handled via Firebase Kotlin SDK initialization

#### Context/Application
- **Android Context** ❌ → **expect/actual** ✅
  - **Pattern**: Abstract platform-specific needs
  ```kotlin
  // commonMain
  expect class PlatformContext
  
  // androidMain
  actual typealias PlatformContext = Context
  
  // iosMain
  actual class PlatformContext { /* iOS-specific context */ }
  ```

## Migration Plan: Detailed Steps

### Step 1: Project Structure Setup

1. **Create shared module structure**:
   ```
   shared/
   ├── build.gradle.kts
   ├── src/
   │   ├── commonMain/
   │   │   ├── kotlin/
   │   │   └── composeResources/ (for Compose Multiplatform Resources)
   │   │       ├── values/
   │   │       │   └── strings.xml
   │   │       └── drawable/
   │   ├── androidMain/kotlin/
   │   ├── iosMain/kotlin/
   │   └── commonTest/kotlin/
   ```

2. **Update `settings.gradle.kts`**:
   - Add shared module
   - Configure iOS targets (iosArm64, iosX64, iosSimulatorArm64)

3. **Create iOS app project** (if starting fresh iOS app):
   - Use Xcode project with CocoaPods or Swift Package Manager
   - Link to shared module framework
   - **Environment Configuration**: Debug and Release only
   - Configure Debug and Release configurations with different `GoogleService-Info.plist` files

### Step 2: Migrate Pure Kotlin Code

**Priority: High | Effort: Low | Risk: Low**

1. **Move domain models to `commonMain`**:
   - Copy `JournalEntry`, `User`, `ErrorReason`
   - **Replace `java.time.LocalDate` with `kotlinx-datetime`**
     - Use ISO 8601 format: `YYYY-MM-DD` for dates
     - Format: `LocalDate.toString()` produces ISO 8601 format
   - Add `kotlinx-datetime` dependency

2. **Move use cases to `commonMain`**:
   - All use cases are pure Kotlin
   - Update dependencies to repository interfaces

3. **Move repository interfaces to `commonMain`**:
   - `MainRepository` interface
   - Keep implementation in `commonMain` after DI migration

4. **Move data models (DTOs) to `commonMain`**:
   - All `@Serializable` DTOs
   - `JournalEntryDto`, `JournalEntriesResponse`, etc.
   - Ensure date fields use ISO 8601 format strings

### Step 3: Migrate Networking Layer (If Needed)

**Priority: Low | Effort: Low | Risk: Low**

**Note**: Primary backend will be Firebase Kotlin SDK, but Ktor client can be kept for future use or alternative endpoints.

1. **Ktor client configuration** (optional, for future use):
   - Keep current `KtorClient.kt` logic
   - Create platform-specific engines:
     ```kotlin
     // androidMain
     actual fun createHttpClient() = HttpClient(Android) { ... }
     
     // iosMain  
     actual fun createHttpClient() = HttpClient(Darwin) { ... }
     ```

2. **API service**:
   - **Primary**: Use Firebase Kotlin SDK for Firestore operations
   - **Alternative**: Keep `KtorApiServiceImpl` as alternative if needed
   - Move `ApiService` interface to `commonMain` if using Ktor
   - Remove or deprecate old `FirestoreServiceImpl` (replaced by Firebase Kotlin SDK)

3. **Error handling**:
   - Move `ErrorHandler`, `ErrorHandlerImpl` to `commonMain`
   - Update error types for platform-agnostic handling

### Step 4: Migrate Dependency Injection

**Priority: High | Effort: Medium | Risk: Medium**

**Decision**: Replace Hilt with Koin (Hilt is Android-only)

1. **Replace Hilt with Koin**:
   - Add Koin dependencies to `commonMain`:
     ```kotlin
     implementation("io.insert-koin:koin-core:3.5.0")
     implementation("io.insert-koin:koin-compose:1.1.0")
     ```
   - Create Koin modules in `commonMain`:
     ```kotlin
     val appModule = module {
       // Firebase Kotlin SDK (shared in commonMain)
       single<FirebaseAuth> { FirebaseAuth.getInstance() }
       single<Firestore> { FirebaseFirestore.getInstance() }
       
       // Auth Service (fully shared using Firebase Kotlin SDK)
       // All auth operations including Google Sign-In via Firebase Kotlin SDK
       single<AuthService> { AuthServiceImpl(get()) }
       
       // API Service (using Firebase Kotlin SDK - fully shared)
       single<ApiService> { FirestoreServiceImpl(get()) }
       
       // Repository
       single<MainRepository> { MainRepositoryImpl(get(), get()) }
     }
     ```

2. **Platform-specific modules**:
   - Use `expect`/`actual` for platform-specific providers
   - Example: Platform context, Google Sign-In clients

3. **Update ViewModels**:
   - Remove `@HiltViewModel` annotations
   - Use `viewModel()` Koin DSL or manual injection
   - Move ViewModels to `commonMain` if using Compose Multiplatform
   - Example: `val viewModel: DetailViewModel = viewModel()`

4. **Update application class**:
   - Initialize Koin in `commonMain` entry point:
     ```kotlin
     fun initKoin() {
       startKoin {
         modules(appModule)
       }
     }
     ```
   - Platform-specific initialization in actual implementations

### Step 5: Migrate Data Persistence

**Priority: Medium | Effort: Medium | Risk: Medium**

**Decision**: Primary storage via Firebase Kotlin SDK (Firestore), Room for local caching if needed

1. **Firebase Kotlin SDK setup**:
   - Add Firebase Kotlin SDK dependencies:
     ```kotlin
     implementation("dev.gitlive:firebase-firestore:4.3.0")
     ```
   - Initialize Firebase in `commonMain`
   - Use Firestore for primary data storage
   - Store dates as ISO 8601 strings

2. **Room KMP setup (Optional - for local caching)**:
   - If keeping Room for offline caching:
     - Add Room KMP dependencies
     - Use `expect`/`actual` for database builder
     - Ensure all DAO methods are `suspend` or return `Flow`
     - Create `expect` function for database builder
     - Android: Use standard Room builder
     - iOS: Use Room with SQLite driver

3. **Entity models**:
   - Firestore documents use `@Serializable` classes
   - Room entities (if used) can stay in `commonMain`
   - Ensure types are KMP-compatible (use `kotlinx-datetime`, ISO 8601 strings)

### Step 6: Migrate Authentication & Backend

**Priority: Medium | Effort: Medium | Risk: Medium**

**Decision: Firebase Kotlin SDK for Authentication and Firestore**

1. **Authentication (Firebase Kotlin SDK)**:
   - **Use Firebase Kotlin SDK for all authentication**:
     - Google Sign-In via Firebase Auth's Google provider (if supported by SDK)
     - Sign in, sign out, current user, token refresh
     - All auth operations in `commonMain`
   - **Setup**:
     - May need platform-specific OAuth client ID configuration
     - Verify Firebase Kotlin SDK fully supports Google Sign-In on both platforms
   - **Pattern**: 
     ```
     Firebase Kotlin SDK Auth (commonMain) → Authenticate (including Google Sign-In) 
     → Firebase Kotlin SDK Firestore (commonMain) → Access Firestore
     ```

2. **Firebase Kotlin SDK Setup**:
   - Add Firebase Kotlin SDK dependencies in `commonMain`:
     ```kotlin
     dependencies {
         // Firebase Auth for authentication (shared)
         implementation("dev.gitlive:firebase-auth:4.3.0")
         // Firestore for data storage (shared)
         implementation("dev.gitlive:firebase-firestore:4.3.0")
     }
     ```
   - Verify all required Firebase features are supported
   - Initialize Firebase in `commonMain`
   - Use Firebase Kotlin SDK in `commonMain` for all auth and Firestore operations

3. **AuthService Interface**:
   - Keep `AuthService` interface in `commonMain`
   - **Fully shared implementation** (`commonMain`):
     - Use Firebase Kotlin SDK for all auth operations
     - Google Sign-In via Firebase Auth's Google provider
     - Auth state management
   - Methods: `signIn()`, `signOut()`, `getCurrentUser()`, `getCurrentToken()`
   - **No platform-specific implementations needed** (unless for OAuth client ID setup)

4. **Firestore Service**:
   - Migrate `FirestoreServiceImpl` to use Firebase Kotlin SDK
   - Code entirely in `commonMain` using Firebase Kotlin SDK
   - Use Firebase Kotlin SDK's reactive APIs (Flow-based) for real-time updates
   - No platform-specific implementations needed

**Benefits**: Fully shared authentication and Firestore logic, maximum code reuse, simpler maintenance

### Step 7: Migrate ViewModels

**Priority: High | Effort: Medium | Risk: Low**

1. **Lifecycle ViewModel**:
   - `androidx.lifecycle:lifecycle-viewmodel-compose` has KMP support
   - ViewModels can move to `commonMain`
   - Update DI to use Koin `viewModel()` DSL
   - Remove `@HiltViewModel` annotations

2. **State management**:
   - `StateFlow`, `MutableStateFlow` work everywhere
   - No changes needed

3. **Coroutine scopes**:
   - Replace `viewModelScope` with platform-agnostic scope
   - Use `expect`/`actual` for coroutine scope provider

4. **Integration with Firebase Kotlin SDK**:
   - Update ViewModels to use Firebase Kotlin SDK for data access
   - Use Firebase Kotlin SDK's reactive APIs (Flow-based) for real-time updates

### Step 8: Migrate UI (Compose Multiplatform)

**Priority: Medium | Effort: High | Risk: Medium**

**Decision**: Use Compose Multiplatform for iOS (shared UI code)

1. **Setup Compose Multiplatform**:
   - Add Compose Multiplatform dependencies
   - Configure iOS target in shared module
   - Add Compose Multiplatform Resources:
     ```kotlin
     implementation(compose.components.resources)
     ```

2. **Migrate Composables**:
   - Most Compose code can move to `commonMain`
   - Use Compose Multiplatform Resources for strings, images, fonts
   - Resources in `commonMain/composeResources/` directory
   - Example: `stringResource(Res.string.app_name)`

3. **Navigation**:
   - Navigation Compose has KMP support
   - Move navigation graph to `commonMain`

4. **Platform-specific UI**:
   - **GIF Picker**: Keep `GiphyPicker.kt` in `androidMain` source set
   - Conditionally show/hide on iOS (not implemented initially)
   - Revisit later for iOS implementation

### Step 9: Platform-Specific Wrappers & Configuration

**Priority: Low | Effort: Low | Risk: Low**

1. **Logging**: `expect`/`actual` for platform logging
   - Android: Timber
   - iOS: NSLog or platform-specific logging

2. **Image Loading**: Coil 3.0 (already multiplatform)
   - Update to `coil-compose` and `coil-network-ktor` artifacts
   - Use directly in `commonMain`

3. **Resources**: Official Kotlin Multiplatform Resources
   - Use Compose Multiplatform Resources
   - Resources in `commonMain/composeResources/` directory
   - Fallback to Moko Resources if needed

4. **Build Configuration**: BuildKonfig plugin
   - **Decision**: Decide which constants to expose during migration
   - **Environments**: Debug and Release only
   - **Constants**: Date format (ISO 8601), API keys, feature flags, version info

5. **Context**: Abstract platform context needs via `expect`/`actual`

## Library Compatibility Matrix

| Library | KMP Support | Migration Required | Alternative |
|---------|------------|-------------------|-------------|
| Ktor Client | ✅ Yes | None (already compatible) | - |
| Kotlinx Serialization | ✅ Yes | None | - |
| Kotlinx Coroutines | ✅ Yes | None | - |
| Jetpack Compose | ✅ Yes (Multiplatform) | Update dependencies | - |
| Hilt | ❌ No | Replace with Koin | Koin, Kodein |
| Room | ⚠️ Experimental | Use expect/actual pattern | SQLDelight, Realm |
| Firebase Auth | ✅ Firebase Kotlin SDK | Use Firebase Kotlin SDK in commonMain | Firebase Kotlin SDK for all auth (including Google Sign-In) |
| Firebase Firestore | ✅ Firebase Kotlin SDK | Use Firebase Kotlin SDK | Firebase Kotlin SDK for Firestore |
| Coil | ✅ Yes (3.0+) | Update to Coil 3.0 for multiplatform | - |
| Timber | ❌ No | expect/actual wrapper | Napier, custom |
| GIPHY SDK | ❌ No | API integration or platform-specific | GIPHY REST API |
| java.time | ❌ No | Replace with kotlinx-datetime | kotlinx-datetime |
| BuildConfig | ❌ No | Use BuildKonfig plugin | BuildKonfig |

## Risk Assessment

### High Risk
- **Room KMP**: Room KMP support is experimental. May encounter stability issues.
- **Firestore iOS**: Configuring Firestore for iOS via CocoaPods requires careful setup.

### Medium Risk
- **Authentication**: Google Sign-In UI requires platform-specific implementation (already verified - see verification doc). Firebase Kotlin SDK supports backend auth operations.
- **DI Migration**: Hilt → Koin requires refactoring all DI code. Testing needed to ensure equivalent behavior.
- **UI Migration**: Compose Multiplatform on iOS is relatively new. May encounter platform-specific issues.
- **Firebase Kotlin SDK**: ✅ Verified - All required features supported (see `FIREBASE_KOTLIN_SDK_VERIFICATION.md`). Only Google Sign-In UI needs platform-specific code (expected).

### Low Risk
- **Networking**: Ktor is battle-tested and fully KMP-compatible.
- **Pure Kotlin Code**: Domain models, use cases have no platform dependencies.

## Testing Strategy

1. **Unit Tests**:
   - Move to `commonTest` source set
   - Run on all platforms (JVM, Android, iOS simulator)

2. **Integration Tests**:
   - Test repository → API service → Ktor client flow
   - Platform-specific tests for DI initialization

3. **UI Tests**:
   - Platform-specific: Android Espresso, iOS XCUITest
   - Compose Multiplatform: Consider shared test framework

## Timeline Estimate

**Note**: As a hobby project with no formal rollout strategy, timeline is flexible. Migrate at your own pace.

| Phase | Duration | Effort |
|-------|----------|--------|
| Setup & Structure | 1-2 days | Low |
| Pure Kotlin Migration | 2-3 days | Low |
| Networking Migration (Firebase Kotlin SDK) | 3-5 days | Medium |
| DI Migration (Hilt → Koin) | 5-7 days | Medium |
| Persistence Migration (Firebase Kotlin SDK) | 3-5 days | Medium |
| Auth Migration (Platform-specific + Firebase Kotlin SDK) | 5-7 days | Medium |
| ViewModel Migration | 3-5 days | Medium |
| UI Migration (Compose Multiplatform) | 10-14 days | High |
| Platform Wrappers & Resources | 3-5 days | Low |
| Testing & Polish | 5-7 days | Medium |
| **Total** | **45-60 days** | **Medium-High** |

**Flexible Approach**: As a hobby project, you can:
- Migrate feature by feature at your own pace
- Skip non-critical phases initially
- Focus on learning and experimentation
- No pressure to complete within timeline

## Next Steps

1. ✅ Research completed (this document)
2. ✅ All decisions made (see "Migration Decisions Summary" above)
3. ✅ All open questions resolved
4. ✅ Ready to start migration
5. ⏭️ **Set up shared module structure**
6. ⏭️ **Begin Phase 1: Pure Kotlin code migration**
7. ⏭️ Create iOS app project (if starting fresh)
8. ⏭️ Verify Firebase Kotlin SDK has all required functionality (during migration)

## Migration Readiness Checklist

- [x] Authentication approach decided (Firebase Kotlin SDK for all authentication including Google Sign-In)
- [x] Backend service approach decided (Firebase Kotlin SDK)
- [x] Resources library decided (Official Kotlin Multiplatform Resources)
- [x] Date format decided (ISO 8601 Standard)
- [x] iOS architecture decided (Compose Multiplatform)
- [x] Image loading decided (Coil 3.0)
- [x] Testing approach decided (Unit tests only for now)
- [x] CI/CD approach decided (Android only for now)
- [x] Development workflow decided (Hybrid approach)
- [x] Build configuration decided (BuildKonfig, decide constants during migration)
- [x] Environment configuration decided (Debug/Release only)
- [x] Rollout strategy decided (No formal strategy needed - hobby project)

**Status**: ✅ **All decisions made - Ready to start migration immediately!**

## References

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Room KMP Support](https://developer.android.com/kotlin/multiplatform/room)
- [Koin KMP](https://insert-koin.io/docs/reference/koin-mp/kmp)
- [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime)

