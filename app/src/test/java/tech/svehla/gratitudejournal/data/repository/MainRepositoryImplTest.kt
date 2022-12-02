package tech.svehla.gratitudejournal.data.repository

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import tech.svehla.gratitudejournal.common.Resource

import tech.svehla.gratitudejournal.data.local.JournalDao
import tech.svehla.gratitudejournal.data.local.entity.JournalEntryEntity
import tech.svehla.gratitudejournal.data.local.entity.toJournalEntryEntity
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.remote.dto.JournalEntryDto
import tech.svehla.gratitudejournal.data.remote.dto.toJournalEntryDto
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.ErrorHandler

class MainRepositoryImplTest {

    private lateinit var mainRepositoryImpl: MainRepositoryImpl
    private lateinit var fakeJournalDao: FakeJournalDao
    private lateinit var fakeApiService: FakeApiService
    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setUp() {
        fakeJournalDao = FakeJournalDao()
        fakeApiService = FakeApiService()
        errorHandler = GeneralErrorHandlerImpl()
        mainRepositoryImpl = MainRepositoryImpl(fakeJournalDao, fakeApiService, errorHandler)
    }

    @Test
    fun `Getting data from empty data sources returns empty response`() = runBlocking {
        mainRepositoryImpl.getJournalEntries().test {
            val expectedResources = listOf(
                Resource.Loading(),
                Resource.Loading(data = emptyList()),
                Resource.Success<List<JournalEntry>>(data = emptyList())
            )
            assertResourceSequence(this, expectedResources)
        }
    }

    @Test
    fun `Getting data returns cached entries`() = runBlocking {
        val localEntries = listOf(testEntryEntity(1), testEntryEntity(2), testEntryEntity(3))
        val resultEntries = localEntries.map { it.toJournalEntry() }
        fakeJournalDao.fakeJournalEntries.addAll(localEntries)

        mainRepositoryImpl.getJournalEntries().test {
            val expectedResources = listOf(
                Resource.Loading(),
                Resource.Loading(data = resultEntries),
                Resource.Success(data = resultEntries)
            )
            assertResourceSequence(this, expectedResources)
        }
    }

    @Test
    fun `Getting data returns remote entries`() = runBlocking {
        val remoteEntries = listOf(testEntryDto(1), testEntryDto(2), testEntryDto(3))
        val resultEntries = remoteEntries.map { it.toJournalEntry() }
        fakeApiService.fakeJournalEntries.addAll(remoteEntries)

        mainRepositoryImpl.getJournalEntries().test {
            val expectedResources = listOf(
                Resource.Loading(),
                Resource.Loading(data = emptyList()),
                Resource.Success(data = resultEntries)
            )
            assertResourceSequence(this, expectedResources)
        }
    }

    @Test
    fun `Getting an entry by a date returns the correct one`() = runBlocking {
        val localEntries = listOf(
            testEntryEntity(1),
            testEntryEntity(2),
            testEntryEntity(3)
        )
        val targetEntry = localEntries[1].toJournalEntry()
        fakeJournalDao.fakeJournalEntries.addAll(localEntries)

        mainRepositoryImpl.getJournalEntry(targetEntry.date).test {
            val expectedResources = listOf(
                Resource.Loading(),
                Resource.Loading(data = targetEntry),
                Resource.Success(data = targetEntry)
            )
            assertResourceSequence(this, expectedResources)
        }
    }

    @Test
    fun `Getting an entry by a date returns null if not found`() = runBlocking {
        val localEntries = listOf(
            testEntryEntity(1),
            testEntryEntity(2),
            testEntryEntity(3)
        )
        fakeJournalDao.fakeJournalEntries.addAll(localEntries)

        mainRepositoryImpl.getJournalEntry(date = "2020-01-05").test {
            val expectedResources = listOf(
                Resource.Loading(),
                Resource.Loading(data = null),
                Resource.Success(data = null)
            )
            assertResourceSequence(this, expectedResources)
        }
    }

    @Test
    fun `Saving an entry actually uploads it and saves to local database`() = runBlocking {
        val entry = testEntry(1)
        mainRepositoryImpl.saveJournalEntry(entry)
        assertThat(fakeApiService.fakeJournalEntries).contains(entry.toJournalEntryDto())
        assertThat(fakeJournalDao.fakeJournalEntries).contains(entry.toJournalEntryEntity())
    }

    @Test
    fun `Clearing a database actually clears it`() = runBlocking {
        val localEntries = listOf(
            testEntryEntity(1),
            testEntryEntity(2),
            testEntryEntity(3)
        )
        fakeJournalDao.fakeJournalEntries.addAll(localEntries)
        assertThat(fakeJournalDao.fakeJournalEntries).hasSize(3)

        mainRepositoryImpl.clearDatabase()
        assertThat(fakeJournalDao.fakeJournalEntries).isEmpty()
    }

    private suspend fun <T> assertResourceSequence(
        flowTurbine: ReceiveTurbine<T>,
        expectedResources: List<T>
    ) {
        expectedResources.forEach {
            val emission = flowTurbine.awaitItem()
            assertThat(emission).isEqualTo(it)
        }
        flowTurbine.awaitComplete()
    }
}

class FakeJournalDao : JournalDao {
    val fakeJournalEntries = mutableListOf<JournalEntryEntity>()

    override suspend fun insertJournalEntries(journalEntries: List<JournalEntryEntity>) {
        fakeJournalEntries.addAll(journalEntries)
    }

    override fun getJournalEntryFlow(entryDate: String): Flow<JournalEntryEntity?> {
        return flow {
            emit(fakeJournalEntries.find { it.date == entryDate })
        }
    }

    override fun getJournalEntriesFlow(): Flow<List<JournalEntryEntity>> {
        return flow {
            emit(fakeJournalEntries)
        }
    }

    override fun getJournalEntry(entryDate: String): JournalEntryEntity? {
        return fakeJournalEntries.find { it.date == entryDate }
    }

    override fun getJournalEntries(): List<JournalEntryEntity> {
        return fakeJournalEntries
    }

    override suspend fun deleteAll() {
        fakeJournalEntries.clear()
    }
}

class FakeApiService : ApiService {
    val fakeJournalEntries = mutableListOf<JournalEntryDto>()

    override suspend fun fetchJournalEntries(): List<JournalEntryDto> {
        return fakeJournalEntries
    }

    override suspend fun fetchJournalEntry(date: String): JournalEntryDto? {
        return fakeJournalEntries.find { it.date == date }
    }

    override suspend fun saveJournalEntry(entry: JournalEntryDto) {
        fakeJournalEntries.add(entry)
    }
}

fun testEntry(index: Int): JournalEntry {
    return JournalEntry(
        date = "2020-01-0$index",
        firstNote = "first note",
        secondNote = "second note",
        thirdNote = "third note"
    )
}

fun testEntryDto(index: Int): JournalEntryDto {
    return JournalEntryDto(
        date = "2020-01-0$index",
        firstNote = "first note",
        secondNote = "second note",
        thirdNote = "third note",
    )
}

fun testEntryEntity(index: Int): JournalEntryEntity {
    return JournalEntryEntity(
        date = "2020-01-0$index",
        firstNote = "first note",
        secondNote = "second note",
        thirdNote = "third note",
    )
}