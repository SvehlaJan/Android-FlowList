package tech.svehla.gratitudejournal.data.remote.implementation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.data.remote.dto.toUser
import tech.svehla.gratitudejournal.domain.model.User


class AuthServiceImpl : AuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)
    override val currentUserFlow: Flow<User?> = _currentUserStateFlow.map { it?.toUser() }

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        firebaseAuth.addAuthStateListener {
            _currentUserStateFlow.tryEmit(it.currentUser)
        }
    }

    override suspend fun signInAnonymously() {
        firebaseAuth.signInAnonymously().await()
    }

    override suspend fun signInWithGoogle(idToken: String) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}