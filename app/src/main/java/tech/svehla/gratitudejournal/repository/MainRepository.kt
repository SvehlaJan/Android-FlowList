package tech.svehla.gratitudejournal.repository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.model.JournalEntry
import tech.svehla.gratitudejournal.network.ApiService
import tech.svehla.gratitudejournal.network.FirestoreService
import tech.svehla.gratitudejournal.persistence.JournalDao
import timber.log.Timber
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService,
    private val journalDao: JournalDao,
    private val firestoreService: FirestoreService
) {

    init {
        Timber.d("Injection MainRepository")
    }

    fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> {
        return networkBoundResource(
            query = { journalDao.getJournalEntries() },
            fetch = { firestoreService.fetchJournalEntries() },
            saveFetchResult = { result -> journalDao.insertJournalEntries(result) },
        )
    }

    fun getJournalEntry(date: String): Flow<Resource<JournalEntry>> {
        return networkBoundResource(
            query = { journalDao.getJournalEntry(date) },
            fetch = { firestoreService.fetchJournalEntry(date) },
            saveFetchResult = { result ->
                result?.let { journalDao.insertJournalEntries(listOf(it)) }
            },
        )
    }

    suspend fun saveJournalEntry(entry: JournalEntry) {
        firestoreService.saveJournalEntry(entry)
        journalDao.insertJournalEntries(listOf(entry))
    }
}
