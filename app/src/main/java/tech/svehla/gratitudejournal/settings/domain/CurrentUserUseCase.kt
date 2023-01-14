package tech.svehla.gratitudejournal.settings.domain

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.settings.domain.model.User
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke(): Flow<User?> {
        return authService.currentUserFlow
    }
}