# KMP Migration: Open Questions & Decisions

**Date**: 2025-10-30  
**Status**: Ready to Start Migration

This document tracks open questions and decisions needed before starting the KMP migration.

## ✅ Decisions Made (11)

1. **Authentication**: Firebase Kotlin SDK ✅
   - **Decision**: Use Firebase Kotlin SDK for all authentication operations
   - **Library**: [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
   - **Support**: Firebase Authentication including Google Sign-In
   - **Implementation**: `implementation("dev.gitlive:firebase-auth:4.3.0")`
   - **Benefits**: 
     - Fully multiplatform authentication (works in `commonMain`)
     - Shared auth logic between Android and iOS
     - Single SDK for all auth operations (no platform-specific Google Sign-In SDKs needed)
   - **Note**: Verify Firebase Kotlin SDK fully supports Google Sign-In on both platforms
   - **Pattern**: Firebase Kotlin SDK Auth → Authenticate (including Google Sign-In) → Firestore access

2. **Backend Service**: Firebase Kotlin SDK for Firestore ✅
   - **Library**: [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
   - **Support**: Firestore operations
   - **Benefits**: Fully multiplatform, shared code in `commonMain`, type-safe with Kotlin Serialization
   - **Note**: Verify all required Firebase features are supported before finalizing
   - **Implementation**: `implementation("dev.gitlive:firebase-firestore:4.3.0")`

3. **Resources**: Official Kotlin Multiplatform Resources ✅
   - **Library**: [Compose Multiplatform Resources](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-setup.html) (Official JetBrains)
   - **Fallback**: Moko Resources if official doesn't meet needs
   - **Setup**: Resources in `commonMain/composeResources/` directory

4. **Date/Time Format**: ISO 8601 Standard ✅
   - **Format**: `YYYY-MM-DD` (dates) or `YYYY-MM-DDTHH:mm:ssZ` (with timezone)
   - **Storage**: ISO 8601 strings in Firestore
   - **Internal**: `kotlinx-datetime` `LocalDate` / `Instant`

5. **iOS Architecture**: Compose Multiplatform ✅
   - Shared UI code between Android and iOS

6. **Image Loading**: Coil 3.0 ✅
   - Supports multiplatform, no replacement needed

7. **GIF Picker**: Android-only for now ✅
   - Keep in `androidMain`, revisit later

8. **Offline/Sync**: Firestore handles automatically ✅
   - Built-in offline persistence and real-time sync

9. **Testing**: Unit tests only for now ✅
   - Run in `commonTest` on all platforms
   - Prepare for integration/UI tests later

10. **CI/CD**: Android only for now ✅
    - JVM unit tests, APK artifacts
    - Add iOS when ready

11. **Development Workflow**: Hybrid approach ✅
    - Primary: JVM unit tests + Android app testing
    - Secondary: iOS testing when needed

## ❓ Remaining Open Questions (3)

### Build & Configuration

#### Q7: BuildKonfig Configuration ✅
**Question**: What build-time constants should be exposed in KMP shared module?

**Decision**: Decide during migration

**Implementation**:
- Will be determined during migration setup
- Likely includes: Date format constants (ISO 8601), API keys, feature flags, version info
- **Note**: Firebase config handled via Firebase Kotlin SDK initialization (platform-specific config files may still be needed)

**Status**: ✅ Resolved - Decide during migration

---

#### Q8: Environment Configuration ✅
**Question**: How should different environments be configured?

**Decision**: Debug and Release only (sufficient for hobby project)

**Implementation**:
- **Environments**: Debug and Release only
- **Configuration Method**: BuildKonfig for KMP shared module constants
- **Firebase Config**: Platform-specific config files
  - Android: Different `google-services.json` for Debug and Release
  - iOS: Different `GoogleService-Info.plist` for Debug and Release
- **Build Variants**: 
  - Android: Debug, Release (with different `google-services.json`)
  - iOS: Debug, Release (with different `GoogleService-Info.plist`)

**Status**: ✅ Resolved - Debug and Release only

---

### Migration & Rollout

#### Q14: Rollout Strategy ✅
**Question**: How should the migration be rolled out?

**Decision**: No formal rollout strategy required (hobby project, won't be published)

**Implementation**:
- Hobby project, most likely won't be published
- Can migrate at own pace without formal rollout strategy
- Flexible approach: migrate feature by feature or all at once, depending on what works best

**Status**: ✅ Resolved - No formal rollout strategy needed

---

## 📋 Summary

### ✅ Ready to Start Migration

**Key Decisions**: 11 decisions made
- Authentication, Backend, Resources, Date Format, iOS Architecture, Image Loading, GIF Picker, Testing, CI/CD, Development Workflow, Offline/Sync

### ⏳ Remaining Questions (0 - All Resolved!)

All questions have been answered:
- **Q7**: BuildKonfig Configuration - Decide during migration ✅
- **Q8**: Environment Configuration - Debug/Release only ✅
- **Q14**: Rollout Strategy - No formal strategy needed (hobby project) ✅

**Status**: ✅ **All questions resolved - Ready to start migration immediately!**

---

## 📋 Action Items

### Before Migration Start
- [x] **Q7**: BuildKonfig - Decide during migration ✅
- [x] **Q8**: Environment Configuration - Debug/Release only ✅
- [x] **Q14**: Rollout Strategy - No formal strategy needed ✅
- [ ] Verify Firebase Kotlin SDK has all required functionality (can do during migration)

### Long-term (Before Release)
- [ ] Configure iOS code signing (when preparing for release - deferred)
- [ ] Expand testing to integration/UI tests when needed
- [ ] Add iOS builds to CI/CD when iOS app is ready

---

## 📝 Notes

- All decisions documented in this file and `KMP_MIGRATION_STRATEGY.md`
- See `KMP_DECISIONS_SUMMARY.md` for quick reference of all decisions
- Update changelog when decisions are made
- Revisit open questions as migration progresses
