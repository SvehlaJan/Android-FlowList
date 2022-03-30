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
import tech.svehla.gratitudejournal.data.local.entity.toJournalEntryEntity
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
    private val apiService: ApiService
) : MainRepository {

    init {
        Timber.d("Injection MainRepository")
    }

    override fun getJournalEntries(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading())

        val journalEntries = journalDao.getJournalEntries().map { it.toJournalEntry() }
        emit(Resource.Loading(data = journalEntries))

        try {
            val remoteJournalEntries = apiService.fetchJournalEntries()
            journalDao.insertJournalEntries(remoteJournalEntries.map { it.toJournalEntryEntity() })
        } catch (e: Exception) {
            when (e) {
                is IOException, is RuntimeException -> {
                    emit(
                        Resource.Error(
                            message = "Oops, something went wrong!",
                            data = journalEntries
                        )
                    )
                }
                is HttpException -> {
                    emit(
                        Resource.Error(
                            message = "Couldn't reach server, check your internet connection.",
                            data = journalEntries
                        )
                    )
                }
                else -> {
                    throw e
                }
            }
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

    override suspend fun saveJournalEntry(entry: JournalEntry) {
        apiService.saveJournalEntry(entry.toJournalEntryDto())
        journalDao.insertJournalEntries(listOf(entry.toJournalEntryEntity()))
        // TODO: Refresh remote data properly
    }

    override suspend fun clearDatabase() {
        journalDao.deleteAll()
    }
}
