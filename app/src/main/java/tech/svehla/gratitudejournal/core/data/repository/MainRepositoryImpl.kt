package tech.svehla.gratitudejournal.core.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tech.svehla.gratitudejournal.core.data.remote.util.Resource
import tech.svehla.gratitudejournal.core.data.local.JournalDao
import tech.svehla.gratitudejournal.core.data.local.entity.toJournalEntryEntity
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.remote.dto.toJournalEntryDto
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import tech.svehla.gratitudejournal.core.domain.repository.MainRepository
import tech.svehla.gratitudejournal.core.domain.util.ErrorHandler
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val apiService: ApiService,
    private val errorHandler: ErrorHandler,
) : MainRepository {

    override fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading())

        val journalEntries = journalDao.getJournalEntries().map { it.toJournalEntry() }
        emit(Resource.Loading(data = journalEntries))

        val remoteResponse = apiService.fetchJournalEntries()
        journalDao.insertJournalEntries(remoteResponse.entries.map { it.toJournalEntryEntity() })

        journalDao.getJournalEntriesFlow().collect { entries ->
            emit(Resource.Success(entries.map { it.toJournalEntry() }))
        }
    }.catch { e ->
        emit(Resource.Error(errorHandler.processError(e)))
    }.flowOn(Dispatchers.IO)

    override fun getJournalEntry(date: String): Flow<Resource<JournalEntry?>> = flow {
        emit(Resource.Loading())

        val journalEntry = journalDao.getJournalEntry(date)?.toJournalEntry()
        emit(Resource.Loading(data = journalEntry))

        val remoteResponse = apiService.fetchJournalEntry(date)
        remoteResponse.entry?.let { remoteJournalEntry ->
            journalDao.insertJournalEntries(listOf(remoteJournalEntry.toJournalEntryEntity()))
        }

        journalDao.getJournalEntryFlow(date).collect {
            emit(Resource.Success(it?.toJournalEntry()))
        }
    }.catch { e ->
        emit(Resource.Error(errorHandler.processError(e)))
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
