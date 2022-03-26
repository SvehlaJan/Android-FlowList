package tech.svehla.gratitudejournal.domain.use_case.settings

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.domain.model.User
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(
    private val authService: AuthService
) {
    operator fun invoke(): Flow<User?> {
        return authService.currentUserFlow
    }
}