package tech.svehla.gratitudejournal.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.model.User

fun FirebaseUser.toUser(): User {
    return User(
        displayName,
        email,
        photoUrl.toString(),
        uid
    )
}

class AuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)
//    private val _currentUserFlow = MutableSharedFlow<FirebaseUser?>(replay = 1)

    val currentUserFlow: Flow<User?> = _currentUserStateFlow.asStateFlow().map { it?.toUser() }

    val currentUserId: String = firebaseAuth.currentUser?.uid ?: ""

    val isLoggedInFlow: Flow<Boolean> = _currentUserStateFlow.asStateFlow().map { it != null }

    init {
        firebaseAuth.addAuthStateListener {
            _currentUserStateFlow.tryEmit(it.currentUser)
//            _currentUserFlow.tryEmit(it.currentUser)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithGoogle(idToken: String) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}