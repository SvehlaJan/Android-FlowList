package tech.svehla.gratitudejournal.core.data.remote.implementation

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.core.data.remote.ApiService
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.core.data.remote.dto.JournalEntryDto
import timber.log.Timber
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(private val authService: AuthService) : ApiService {
    private val db = Firebase.firestore

    override suspend fun fetchJournalEntries(): List<JournalEntryDto> =
        withContext(Dispatchers.IO) {
            val userId = authService.currentUserId ?: return@withContext emptyList()
            return@withContext db.collection("users").document(userId).collection("flow_notes")
                .get()
                .await()
                .documents
                .map {
                    Timber.d("Journal entry: ${it.id}")
                    it.toObject(JournalEntryDto::class.java)!!
                }
        }

    override suspend fun fetchJournalEntry(date: String): JournalEntryDto? =
        withContext(Dispatchers.IO) {
            val userId = authService.currentUserId ?: return@withContext null
            return@withContext db.collection("users").document(userId).collection("flow_notes")
                .document(date)
                .get()
                .await()
                .toObject(JournalEntryDto::class.java)
        }

    override suspend fun saveJournalEntry(entry: JournalEntryDto) = withContext(Dispatchers.IO) {
        val userId = authService.currentUserId ?: return@withContext
        db.collection("users").document(userId).collection("flow_notes")
            .document(entry.date)
            .set(entry)
            .await()
    }
}