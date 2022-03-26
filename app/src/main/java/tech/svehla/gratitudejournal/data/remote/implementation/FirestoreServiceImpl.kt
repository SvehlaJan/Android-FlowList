package tech.svehla.gratitudejournal.data.remote.implementation

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.data.remote.ApiService
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.data.remote.dto.JournalEntryDto
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Query.asFlow(): Flow<QuerySnapshot> {
    return callbackFlow {
        val callback = addSnapshotListener { querySnapshot, ex ->
            if (ex != null) {
                close(ex)
            } else {
                trySend(querySnapshot!!).isSuccess
            }
        }
        awaitClose {
            callback.remove()
        }
    }
}

fun DocumentReference.asFlow(): Flow<DocumentSnapshot> {
    return callbackFlow {
        val callback = addSnapshotListener { documentSnapshot, ex ->
            if (ex != null) {
                close(ex)
            } else {
                trySend(documentSnapshot!!).isSuccess
            }
        }
        awaitClose {
            callback.remove()
        }
    }
}

private fun Query.paginate(lastVisibleItem: Flow<Int>): Flow<List<DocumentSnapshot>> = flow {
    val documents = mutableListOf<DocumentSnapshot>()
    documents.addAll(
        suspendCoroutine { c ->
            this@paginate.limit(25).get().addOnSuccessListener { c.resume(it.documents) }
        }
    )
    emit(documents)
    lastVisibleItem.transform { lastVisible ->
        if (lastVisible == documents.size && documents.size > 0) {
            documents.addAll(
                suspendCoroutine { c ->
                    this@paginate.startAfter(documents.last())
                        .limit(25)
                        .get()
                        .addOnSuccessListener {
                            c.resume(it.documents)
                        }
                }
            )
            emit(documents)
        }
    }.collect { docs ->
        emit(docs)
    }
}

class FirestoreServiceImpl @Inject constructor(private val authService: AuthService) : ApiService {
    private val db = Firebase.firestore

    override suspend fun fetchJournalEntries(): List<JournalEntryDto> {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .get()
            .await()
            .documents
            .map {
                Timber.d("Journal entry: ${it.id}")
                it.toObject(JournalEntryDto::class.java)!!
            }
    }

    override suspend fun fetchJournalEntry(date: String): JournalEntryDto? {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .document(date)
            .get()
            .await()
            .toObject(JournalEntryDto::class.java)
    }

    override suspend fun saveJournalEntry(entry: JournalEntryDto) {
        db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .document(entry.date)
            .set(entry)
            .await()
    }

    fun fetchJournalEntriesAsFlow(): Flow<List<JournalEntry>> {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .asFlow()
            .transform { querySnapshot ->
                querySnapshot.documents.map {
                    it.toObject(JournalEntry::class.java)
                }
            }
    }

    fun fetchJournalEntryAsFlow(date: String): Flow<JournalEntry> {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .document(date)
            .asFlow()
            .map {
                it.toObject(JournalEntry::class.java)!!
            }
    }

//    fun getJournalEntries() = flow {
//        val journalEntries = db.collection("journalEntries").get()
//        journalEntries.addOnSuccessListener {
//            for (document in it) {
//                emit(document.toObject(JournalEntry::class.java))
//            }
//        }
//    }
}