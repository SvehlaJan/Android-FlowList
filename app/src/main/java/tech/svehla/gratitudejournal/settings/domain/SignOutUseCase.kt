package tech.svehla.gratitudejournal.settings.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authService: AuthService,
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            authService.signOut()
            mainRepository.clearDatabase()
        }
    }
}