package tech.svehla.gratitudejournal.data.remote

import tech.svehla.gratitudejournal.data.remote.dto.JournalEntryDto

interface ApiService {
    suspend fun fetchJournalEntries(): List<JournalEntryDto>

    suspend fun fetchJournalEntry(date: String): JournalEntryDto?

    suspend fun saveJournalEntry(entry: JournalEntryDto)
}