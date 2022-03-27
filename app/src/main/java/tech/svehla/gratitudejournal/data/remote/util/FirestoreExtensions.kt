package tech.svehla.gratitudejournal.data.remote.util

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
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