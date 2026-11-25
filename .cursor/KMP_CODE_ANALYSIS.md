# KMP Migration: Code Analysis Summary

**Quick Reference**: What can stay vs. what must change when migrating to Kotlin Multiplatform.

## ✅ Code That Can Stay (KMP-Compatible)

### Pure Kotlin Code (Move to `commonMain`)

#### Domain Layer
- `core/domain/model/JournalEntry.kt` - ✅ Move to `commonMain`
  - ⚠️ Replace `java.time.LocalDate` with `kotlinx-datetime`
- `core/domain/model/ErrorReason.kt` - ✅ Move to `commonMain`
- `core/domain/model/User.kt` - ✅ Move to `commonMain`
- `core/domain/repository/MainRepository.kt` - ✅ Move to `commonMain`

#### Use Cases
- `detail/domain/GetDetailUseCase.kt` - ✅ Move to `commonMain`
- `detail/domain/SaveEntryUseCase.kt` - ✅ Move to `commonMain`
- `history/domain/GetHistoryUseCase.kt` - ✅ Move to `commonMain`
- `settings/domain/SignInWithGoogleUseCase.kt` - ✅ Move to `commonMain`
- `settings/domain/SignOutUseCase.kt` - ✅ Move to `commonMain`
- `settings/domain/CurrentUserUseCase.kt` - ✅ Move to `commonMain`

#### Data Layer (DTOs)
- `core/data/remote/dto/JournalEntryDto.kt` - ✅ Move to `commonMain`
- `core/data/remote/dto/JournalEntriesResponse.kt` - ✅ Move to `commonMain`
- `core/data/remote/dto/JournalEntryResponse.kt` - ✅ Move to `commonMain`
- All `@Serializable` classes - ✅ Move to `commonMain`

#### Error Handling
- `core/data/remote/util/Resource.kt` - ✅ Move to `commonMain`
- `core/data/remote/util/ErrorHandler.kt` - ✅ Move to `commonMain`
- `core/data/remote/util/ErrorHandlerImpl.kt` - ✅ Move to `commonMain`
  - Update `HttpURLConnection` constants to platform-agnostic error types

#### Repository Implementation
- `core/data/repository/MainRepositoryImpl.kt` - ✅ Move to `commonMain` (after DI migration)

### Libraries (Already KMP-Compatible)

#### Networking
- **Ktor Client** ✅
  - `core/data/remote/util/KtorClient.kt` - ✅ Move to `commonMain`
  - `core/data/remote/implementation/KtorApiServiceImpl.kt` - ✅ Move to `commonMain`
  - Only need platform-specific engines:
    - `androidMain`: `HttpClient(Android)`
    - `iosMain`: `HttpClient(Darwin)`

#### Serialization
- **Kotlinx Serialization** ✅
  - All DTOs already use `@Serializable`
  - No changes needed

#### Coroutines
- **Kotlinx Coroutines** ✅
  - All `Flow`, `StateFlow`, `suspend` functions work everywhere
  - No changes needed

#### UI (Compose Multiplatform)
- **Jetpack Compose** ✅ (with Compose Multiplatform)
  - All Composables can move to `commonMain`
  - Platform-specific: Resource loading (strings, images)
  - Navigation Compose also has KMP support

## ⚠️ Code That Needs Changes

### Dependency Injection

#### Files to Refactor
- `core/di/NetworkModule.kt` - ❌ Replace Hilt with Koin
- `core/di/PersistenceModule.kt` - ❌ Replace Hilt with Koin
- `core/di/RepositoryModule.kt` - ❌ Replace Hilt with Koin
- `core/di/ViewModelModule.kt` - ❌ Replace Hilt with Koin

#### ViewModels
- `history/presentation/HistoryViewModel.kt` - ⚠️ Remove `@HiltViewModel`, use Koin
- `detail/presentation/DetailViewModel.kt` - ⚠️ Remove `@HiltViewModel`, use Koin
- `settings/presentation/SettingsViewModel.kt` - ⚠️ Remove `@HiltViewModel`, use Koin

**Migration**: Replace all `@Module`, `@Provides`, `@Inject`, `@HiltViewModel` with Koin `module { }` DSL.

### Data Persistence

#### Room Database
- `core/data/local/AppDatabase.kt` - ⚠️ Use `expect`/`actual` pattern
- `core/data/local/JournalDao.kt` - ✅ Move to `commonMain` (ensure all methods are `suspend` or return `Flow`)
- `core/data/local/entity/JournalEntryEntity.kt` - ⚠️ Replace `java.time.LocalDate` with `kotlinx-datetime`
- `core/di/PersistenceModule.kt` - ⚠️ Use `expect`/`actual` for database builder

**Migration**: Use Room KMP support with `expect`/`actual` pattern for platform-specific database builders.

### Authentication

#### Firebase Auth → Platform-Specific Google Sign-In ✅
- `core/data/remote/AuthService.kt` - ✅ Interface stays in `commonMain`
- `core/data/remote/implementation/AuthServiceImpl.kt` - ⚠️ Keep platform-specific implementations
- `settings/presentation/SettingsViewModel.kt` - ⚠️ Update to use abstracted `AuthService`
- `core/presentation/ui/util/GoogleSignInClient.kt` - ⚠️ Platform-specific Google Sign-In UI

**Migration**: 
- **Decision**: Keep authentication platform-specific with Google Sign-In SDKs
- Keep `AuthService` interface in `commonMain`
- Android: Keep existing Google Sign-In SDK integration
- iOS: Implement Google Sign-In iOS SDK
- Extract ID token and use with Firebase Auth on each platform

### Backend Service

#### Firebase Firestore → Keep with iOS Support ✅
- `core/data/remote/ApiService.kt` - ✅ Interface stays in `commonMain`
- `core/data/remote/implementation/FirestoreServiceImpl.kt` - ⚠️ Make multiplatform via expect/actual
- `core/data/remote/implementation/KtorApiServiceImpl.kt` - ✅ Keep as alternative (if needed)

**Migration**: 
- **Decision**: Keep using Firestore (feasible for iOS via CocoaPods)
- Keep `FirestoreServiceImpl` pattern but make it multiplatform
- Android: Use Firebase Android SDK (existing)
- iOS: Use Firebase iOS SDK via CocoaPods in `build.gradle.kts`
- Use `expect`/`actual` pattern for Firestore database access

### Date/Time

#### Files to Update
- `core/domain/model/JournalEntry.kt` - ⚠️ Replace `java.time.LocalDate` with `kotlinx-datetime`
- `core/data/local/entity/JournalEntryEntity.kt` - ⚠️ Replace `java.time.LocalDate` with `kotlinx-datetime`
- `core/presentation/MainSkelet.kt` - ⚠️ Replace `java.time.LocalDate` with `kotlinx-datetime`

**Migration**: Add `kotlinx-datetime` dependency and replace all `java.time.*` with `kotlinx.datetime.*`.

### Logging

#### Timber (Android-Only)
- All `Timber.d()`, `Timber.e()` calls - ⚠️ Replace with `expect`/`actual` logging

**Migration**: 
```kotlin
// commonMain
expect fun logDebug(tag: String, message: String)
expect fun logError(tag: String, message: String, throwable: Throwable?)

// androidMain
actual fun logDebug(tag: String, message: String) = Timber.d("$tag: $message")

// iosMain
actual fun logDebug(tag: String, message: String) = NSLog("[$tag] $message")
```

### Image Loading

#### Coil (✅ KMP-Compatible)
- All `rememberAsyncImagePainter()`, `AsyncImage()` - ✅ Coil 3.0 supports multiplatform
- `core/presentation/ui/components/DetailScreen.kt` - Uses Coil for GIF loading

**Migration**: 
- ✅ No replacement needed! Coil 3.0 has multiplatform support
- Update to `coil-compose` and `coil-network-ktor` artifacts
- Can move Coil usage directly to `commonMain`

### GIF Picker

#### GIPHY SDK (Android-Only, Deferred)
- `core/presentation/ui/components/GiphyPicker.kt` - ⚠️ Keep Android-only for now

**Migration**:
- **Decision**: Keep GIF picker Android-only, revisit later
- Keep `GiphyPicker.kt` in `androidMain` source set
- Conditionally show/hide on iOS (not implemented initially)
- **Future**: May implement Compose Multiplatform picker or GIPHY API integration later

### Build Configuration

#### BuildConfig (Android-Only)
- `app/build.gradle.kts` - `buildConfigField` usage - ⚠️ Replace with BuildKonfig plugin

**Migration**: Use [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) plugin for KMP shared module.

### Context/Application

#### Android Context
- `core/di/PersistenceModule.kt` - `Application` parameter - ⚠️ Abstract via `expect`/`actual`

**Migration**: 
```kotlin
// commonMain
expect class PlatformContext

// androidMain
actual typealias PlatformContext = Context

// iosMain
actual class PlatformContext { /* iOS-specific */ }
```

## 📊 Migration Checklist

### Phase 1: Pure Kotlin Code ✅
- [ ] Move domain models to `commonMain`
- [ ] Replace `java.time.LocalDate` with `kotlinx-datetime`
- [ ] Move use cases to `commonMain`
- [ ] Move repository interfaces to `commonMain`
- [ ] Move DTOs to `commonMain`

### Phase 2: Networking ✅
- [ ] Move Ktor client to `commonMain`
- [ ] Move `KtorApiServiceImpl` to `commonMain`
- [ ] Remove `FirestoreServiceImpl`
- [ ] Add platform-specific Ktor engines

### Phase 3: Dependency Injection ⚠️
- [ ] Replace Hilt with Koin
- [ ] Create Koin modules in `commonMain`
- [ ] Update ViewModels (remove `@HiltViewModel`)
- [ ] Use `expect`/`actual` for platform-specific providers

### Phase 4: Data Persistence ⚠️
- [ ] Migrate Room to KMP (use `expect`/`actual`)
- [ ] Ensure all DAO methods are `suspend` or return `Flow`
- [ ] Replace `java.time` in entities

### Phase 5: Authentication ⚠️
- [ ] Keep `AuthService` interface in `commonMain`
- [ ] Android: Keep Firebase Auth implementation
- [ ] iOS: Implement iOS-specific auth

### Phase 6: Platform Wrappers ⚠️
- [ ] Replace Timber with `expect`/`actual` logging
- [ ] Update Coil to 3.0 for multiplatform support
- [ ] Replace BuildConfig with BuildKonfig
- [ ] Abstract platform context
- [ ] Keep GIF picker Android-only (defer iOS implementation)

### Phase 7: UI (Compose Multiplatform) ⚠️
- [ ] Add Compose Multiplatform dependencies
- [ ] Move Composables to `commonMain`
- [ ] Handle platform-specific resources
- [ ] Update navigation for KMP

## Estimated File Changes

- **Pure Kotlin Code**: ~15 files (move to `commonMain`, minor date/time changes)
- **DI Migration**: ~8 files (Hilt → Koin)
- **Platform-Specific Wrappers**: ~10 files (logging, image loading, context)
- **Total**: ~33 files affected

## Risk Levels

- **Low Risk**: Pure Kotlin code migration, networking (Ktor), image loading (Coil 3.0)
- **Medium Risk**: DI migration (Hilt → Koin), ViewModels, authentication (Ktor OAuth2)
- **High Risk**: Room KMP (experimental), UI migration (Compose Multiplatform)

## Open Questions & Decisions Needed

### Authentication & Backend
- [x] **Authentication**: ✅ Use Ktor for authentication (OAuth2/JWT)
- [x] **Backend Service**: ✅ Use Ktor API implementation
- [ ] **OAuth2 Flow**: What OAuth2 flow will be used? (Authorization Code, Client Credentials, JWT Bearer?)
- [ ] **Token Storage**: Secure storage mechanism for tokens (expect/actual pattern with Keychain/Keystore?)
- [ ] **Token Refresh**: Automatic token refresh logic and retry mechanism?
- [ ] **Backend API Structure**: REST API endpoints, request/response format, error handling?

### Platform-Specific
- [x] **GIF Picker**: ✅ Android-only for now (deferred)
- [ ] **Google Sign-In on iOS**: Use Google Sign-In iOS SDK or WebView OAuth flow?
- [ ] **iOS App Structure**: Will iOS app use Compose Multiplatform or native SwiftUI?
- [ ] **Platform Resources**: How to handle platform-specific strings, images, configurations?

### Testing & CI
- [ ] **Testing Strategy**: Unit tests in `commonTest`, platform-specific UI tests?
- [ ] **CI/CD**: Build both Android and iOS in CI? Which platforms to target?
- [ ] **Development Workflow**: How to test KMP shared module independently?

### Data & Storage
- [ ] **Date Format**: Standard date format for API communication (ISO 8601?)
- [ ] **Offline Support**: Caching strategy, sync mechanism between local DB and API?
- [ ] **Migration Strategy**: How to handle existing user data during migration?

### Build & Configuration
- [ ] **Build Config**: BuildKonfig plugin configuration (which constants to expose?)
- [ ] **Environment Configuration**: Debug vs Release API endpoints, feature flags?
- [ ] **Code Signing**: iOS code signing strategy for development and release?

