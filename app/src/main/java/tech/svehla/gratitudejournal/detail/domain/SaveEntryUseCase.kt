package tech.svehla.gratitudejournal.detail.domain

import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import javax.inject.Inject

class SaveEntryUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(entry: JournalEntry) {
        return mainRepository.saveJournalEntry(entry)
    }
}