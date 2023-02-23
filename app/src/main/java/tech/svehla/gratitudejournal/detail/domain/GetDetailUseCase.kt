package tech.svehla.gratitudejournal.detail.domain

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.core.data.remote.util.Resource
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    operator fun invoke(date: String): Flow<Resource<JournalEntry?>> {
        return mainRepository.getJournalEntry(date)
    }
}