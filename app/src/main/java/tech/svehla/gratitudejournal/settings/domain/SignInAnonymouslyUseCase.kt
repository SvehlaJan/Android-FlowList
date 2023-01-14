package tech.svehla.gratitudejournal.settings.domain

import tech.svehla.gratitudejournal.core.data.remote.AuthService
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke() {
        authService.signInAnonymously()
    }
}