package tech.svehla.gratitudejournal.core.data.remote

import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntriesResponse
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntryDto
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntryResponse

interface ApiService {
    suspend fun fetchJournalEntries(): JournalEntriesResponse

    suspend fun fetchJournalEntry(date: String): JournalEntryResponse

    suspend fun saveJournalEntry(entry: JournalEntryDto)
}