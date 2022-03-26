package tech.svehla.gratitudejournal.domain.use_case.settings

import tech.svehla.gratitudejournal.data.remote.AuthService
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke() {
        authService.signOut()
    }
}