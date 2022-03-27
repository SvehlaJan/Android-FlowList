package tech.svehla.gratitudejournal.domain.use_case.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.domain.repository.MainRepository
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