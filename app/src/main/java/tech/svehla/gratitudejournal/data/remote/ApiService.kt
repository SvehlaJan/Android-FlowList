package tech.svehla.gratitudejournal.data.remote

import retrofit2.http.GET
import tech.svehla.gratitudejournal.domain.model.JournalEntry

interface ApiService {

  @GET("DisneyPosters2.json")
  suspend fun fetchJournalEntries(): List<JournalEntry>

  @GET("DisneyPosters2.json")
  suspend fun fetchJournalEntry(entryDate: String): JournalEntry
}
