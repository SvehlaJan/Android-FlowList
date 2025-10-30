package tech.svehla.gratitudejournal.core.data.remote.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
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

    companion object {
        private const val BASE_URL = "http://192.168.68.62:8080"
    }

    @Throws(CancellationException::class, ResponseException::class, ClientRequestException::class)
    override suspend fun fetchJournalEntries(): JournalEntriesResponse = withContext(Dispatchers.IO) {
        val response: HttpResponse = client.get("$BASE_URL/entries") {
            header("Authorization", "Bearer ${authService.currentToken}")
        }
        val entries: JournalEntriesResponse = response.body<JournalEntriesResponse>()

        return@withContext entries
    }

    override suspend fun fetchJournalEntry(date: String): JournalEntryResponse = withContext(Dispatchers.IO) {
        val response: HttpResponse = client.get("$BASE_URL/entries/$date") {
            header("Authorization", "Bearer ${authService.currentToken}")
        }
        val entry: JournalEntryResponse = response.body<JournalEntryResponse>()

        return@withContext entry
    }

    override suspend fun saveJournalEntry(entry: JournalEntryDto) = withContext(Dispatchers.IO) {
        val url = Url("$BASE_URL/entries")
        client.post(url) {
            header("Authorization", "Bearer ${authService.currentToken}")
            setBody(entry)
        }
        Unit
    }
}