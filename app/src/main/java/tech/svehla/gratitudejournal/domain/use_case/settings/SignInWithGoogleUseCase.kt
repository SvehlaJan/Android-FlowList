package tech.svehla.gratitudejournal.domain.use_case.settings

import tech.svehla.gratitudejournal.data.remote.AuthService
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authService: AuthService
) {
    suspend operator fun invoke(idToken: String) {
        authService.signInWithGoogle(idToken)
    }
}