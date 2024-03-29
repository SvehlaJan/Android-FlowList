package tech.svehla.gratitudejournal.core.data.remote.implementation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.core.data.remote.AuthService
import tech.svehla.gratitudejournal.core.data.remote.dto.toUser
import tech.svehla.gratitudejournal.settings.domain.model.User


class AuthServiceImpl : AuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)
    override val currentUserFlow: Flow<User?> = _currentUserStateFlow.map { it?.toUser() }
    private var _currentToken: String? = null
    override val currentToken: String?
        get() = _currentToken

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        firebaseAuth.addAuthStateListener {
            _currentUserStateFlow.tryEmit(it.currentUser)
            it.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentToken = task.result?.token
                }
            }
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