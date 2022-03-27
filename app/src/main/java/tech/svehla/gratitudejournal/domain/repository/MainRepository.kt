package tech.svehla.gratitudejournal.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry

interface MainRepository {
    fun getJournalEntries(): Flow<Resource<List<JournalEntry>>>
    fun getJournalEntry(date: String): Flow<Resource<JournalEntry>>

    suspend fun clearDatabase()
    suspend fun saveJournalEntry(entry: JournalEntry)
}