package tech.svehla.gratitudejournal.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.local.entity.toJournalEntryEntity
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.remote.dto.toJournalEntryDto
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val apiService: ApiService,
) : MainRepository {

    override fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading())

        val journalEntries = journalDao.getJournalEntries().map { it.toJournalEntry() }
        emit(Resource.Loading(data = journalEntries))

        val remoteJournalEntries = apiService.fetchJournalEntries()
        journalDao.insertJournalEntries(remoteJournalEntries.map { it.toJournalEntryEntity() })

        journalDao.getJournalEntriesFlow().collect { entries ->
            emit(Resource.Success(entries.map { it.toJournalEntry() }))
        }
    }.catch { e ->
        emit(Resource.Error(e))
    }.flowOn(Dispatchers.IO)

    override fun getJournalEntry(date: String): Flow<Resource<JournalEntry?>> = flow {
        emit(Resource.Loading())

        val journalEntry = journalDao.getJournalEntry(date)?.toJournalEntry()
        emit(Resource.Loading(data = journalEntry))

        apiService.fetchJournalEntry(date)?.let { remoteJournalEntry ->
            journalDao.insertJournalEntries(listOf(remoteJournalEntry.toJournalEntryEntity()))
        }

        journalDao.getJournalEntryFlow(date).collect {
            emit(Resource.Success(it?.toJournalEntry()))
        }
    }.catch { e ->
        emit(Resource.Error(e))
    }.flowOn(Dispatchers.IO)

    override suspend fun saveJournalEntry(entry: JournalEntry) {
        apiService.saveJournalEntry(entry.toJournalEntryDto())
        journalDao.insertJournalEntries(listOf(entry.toJournalEntryEntity()))
        // TODO: Refresh remote data properly
    }

    override suspend fun clearDatabase() {
        journalDao.deleteAll()
    }
}
