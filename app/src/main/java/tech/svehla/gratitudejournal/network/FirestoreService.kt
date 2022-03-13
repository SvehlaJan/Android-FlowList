package tech.svehla.gratitudejournal.network

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.model.FirestoreJournalEntry
import tech.svehla.gratitudejournal.model.JournalEntry
import tech.svehla.gratitudejournal.model.toFirestoreJournalEntry
import tech.svehla.gratitudejournal.model.toJournalEntry
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

class FirestoreService @Inject constructor(private val authService: AuthService) {
    private val db = Firebase.firestore

    suspend fun fetchJournalEntries(): List<JournalEntry> {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .get()
            .await()
            .documents
            .map {
                Timber.d("Journal entry: ${it.id}")
                it.toObject(FirestoreJournalEntry::class.java)!!.toJournalEntry()
            }
    }

    suspend fun fetchJournalEntry(date: String): JournalEntry? {
        return db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .document(date)
            .get()
            .await()
            .toObject(FirestoreJournalEntry::class.java)?.toJournalEntry()
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

    suspend fun saveJournalEntry(entry: JournalEntry) {
        val firebaseEntry = entry.toFirestoreJournalEntry()
        db.collection("users").document(authService.currentUserId).collection("flow_notes")
            .document(firebaseEntry.date)
            .set(firebaseEntry)
            .await()
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