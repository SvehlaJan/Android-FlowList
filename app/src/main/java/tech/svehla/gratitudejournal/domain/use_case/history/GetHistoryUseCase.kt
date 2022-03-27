package tech.svehla.gratitudejournal.domain.use_case.history

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    val refreshRequired: Flow<Unit> = mainRepository.refreshRequiredSharedFlow

    operator fun invoke(): Flow<Resource<List<JournalEntry>>> {
        return mainRepository.getJournalEntries()
    }
}