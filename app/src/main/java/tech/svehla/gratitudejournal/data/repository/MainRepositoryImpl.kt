package tech.svehla.gratitudejournal.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.data.remote.dto.toJournalEntryDto
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val apiService: ApiService,
    private val authService: AuthService
) : MainRepository {

    private val _refreshRequiredSharedFlow = MutableSharedFlow<Unit>(replay = 0)
    override val refreshRequiredSharedFlow: SharedFlow<Unit> = _refreshRequiredSharedFlow

    init {
        Timber.d("Injection MainRepository")
        GlobalScope.launch(Dispatchers.IO) {
            authService.userChangedFlow.collect {
                Timber.d("Current user changed")
                _refreshRequiredSharedFlow.emit(Unit)
            }
        }
    }

    override fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading())

        val journalEntries = journalDao.getJournalEntries().map { it.toJournalEntry() }
        emit(Resource.Loading(data = journalEntries))

        try {
            val remoteJournalEntries = apiService.fetchJournalEntries()
            journalDao.insertJournalEntries(remoteJournalEntries.map { it.toJournalEntryEntity() })
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    message = "Oops, something went wrong!",
                    data = journalEntries
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    message = "Couldn't reach server, check your internet connection.",
                    data = journalEntries
                )
            )
        }

        val newJournalEntries = journalDao.getJournalEntries().map { it.toJournalEntry() }
        emit(Resource.Success(newJournalEntries))
    }.flowOn(Dispatchers.IO)

    override fun getJournalEntry(date: String): Flow<Resource<JournalEntry>> = flow {
        emit(Resource.Loading())

        val journalEntry = journalDao.getJournalEntry(date)?.toJournalEntry()
        emit(Resource.Loading(data = journalEntry))

        try {
            apiService.fetchJournalEntry(date)?.let { remoteJournalEntry ->
                journalDao.insertJournalEntries(listOf(remoteJournalEntry.toJournalEntryEntity()))
            }
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    message = "Oops, something went wrong!",
                    data = journalEntry
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    message = "Couldn't reach server, check your internet connection.",
                    data = journalEntry
                )
            )
        }

        val newWordInfo = journalDao.getJournalEntry(date)?.toJournalEntry()
        emit(Resource.Success(newWordInfo))
    }.flowOn(Dispatchers.IO)

//    override fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> {
//        return networkBoundResource(
//            query = { journalDao.getJournalEntries().map { it.map { item -> item.toJournalEntry() } } },
//            fetch = { firestoreService.fetchJournalEntries().map { it.toJournalEntry() } },
//            saveFetchResult = { result -> journalDao.insertJournalEntries(result) },
//        )
//    }

//    override fun getJournalEntry(date: String): Flow<Resource<JournalEntry>> {
//        return networkBoundResource(
//            query = { journalDao.getJournalEntry(date) },
//            fetch = { firestoreService.fetchJournalEntry(date) },
//            saveFetchResult = { result ->
//                result?.let { journalDao.insertJournalEntries(listOf(it)) }
//            },
//        )
//    }

    override suspend fun saveJournalEntry(entry: JournalEntry) {
        apiService.saveJournalEntry(entry.toJournalEntryDto())
        _refreshRequiredSharedFlow.emit(Unit)
    }

    override suspend fun clearDatabase() {
        journalDao.deleteAll()
    }
}
