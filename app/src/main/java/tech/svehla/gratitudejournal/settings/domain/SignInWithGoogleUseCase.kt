package tech.svehla.gratitudejournal.settings.domain

import tech.svehla.gratitudejournal.core.data.remote.AuthService
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(idToken: String) {
        authService.signInWithGoogle(idToken)
    }
}