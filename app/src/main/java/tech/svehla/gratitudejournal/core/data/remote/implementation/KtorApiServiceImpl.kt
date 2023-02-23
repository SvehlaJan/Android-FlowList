package tech.svehla.gratitudejournal.core.data.remote.implementation

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntriesResponse
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntryDto
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntryResponse
import javax.inject.Inject

class KtorApiServiceImpl @Inject constructor(
    private val authService: AuthService,
    private val client: HttpClient,
) : ApiService {

    @Throws(CancellationException::class, ResponseException::class, ClientRequestException::class)
    override suspend fun fetchJournalEntries(): JournalEntriesResponse = withContext(Dispatchers.IO) {
        val response: JournalEntriesResponse = client.get("http://192.168.68.62:8080/entries") {
            header("Authorization", "Bearer ${authService.currentToken}")
        }

        return@withContext response
    }

    override suspend fun fetchJournalEntry(date: String): JournalEntryResponse = withContext(Dispatchers.IO) {
        val response: JournalEntryResponse = client.get("http://192.168.68.62:8080/entries/$date") {
            header("Authorization", "Bearer ${authService.currentToken}")
        }

        return@withContext response
    }

    override suspend fun saveJournalEntry(entry: JournalEntryDto) = withContext(Dispatchers.IO) {
        client.post<JournalEntryDto>("http://192.168.68.62:8080/entries") {
            header("Authorization", "Bearer ${authService.currentToken}")
            body = entry
        }
        Unit
    }
}