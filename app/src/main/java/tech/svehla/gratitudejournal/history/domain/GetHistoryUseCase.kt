package tech.svehla.gratitudejournal.history.domain

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.core.data.model.Resource
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    operator fun invoke(): Flow<Resource<List<JournalEntry>>> {
        return mainRepository.getJournalEntries()
    }
}