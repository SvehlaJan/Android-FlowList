package tech.svehla.gratitudejournal.data.remote.implementation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.domain.model.User

fun FirebaseUser.toUser(): User {
    return User(
        displayName,
        email,
        photoUrl.toString(),
        uid
    )
}

class AuthServiceImpl: AuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)

    override val currentUserFlow: Flow<User?> = _currentUserStateFlow.asStateFlow().map { it?.toUser() }

    override val currentUserId: String = firebaseAuth.currentUser?.uid ?: ""

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

    override suspend fun signInWithGoogle(idToken: String) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}