package tech.svehla.gratitudejournal.domain.use_case.detail

import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import javax.inject.Inject

class SaveEntryUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(entry: JournalEntry) {
        return mainRepository.saveJournalEntry(entry)
    }
}