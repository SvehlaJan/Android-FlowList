package tech.svehla.gratitudejournal.core.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.core.data.model.Resource
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry

interface MainRepository {
    fun getJournalEntries(): Flow<Resource<List<JournalEntry>>>
    fun getJournalEntry(date: String): Flow<Resource<JournalEntry?>>

    suspend fun clearDatabase()
    suspend fun saveJournalEntry(entry: JournalEntry)
}