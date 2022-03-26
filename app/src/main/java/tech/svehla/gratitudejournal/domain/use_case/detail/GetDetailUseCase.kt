package tech.svehla.gratitudejournal.domain.use_case.detail

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {

    operator fun invoke(date: String): Flow<Resource<JournalEntry>> {
        return mainRepository.getJournalEntry(date)
    }
}