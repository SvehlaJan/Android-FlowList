package tech.svehla.gratitudejournal.data.remote.implementation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import tech.svehla.gratitudejournal.data.remote.AuthService
import tech.svehla.gratitudejournal.data.remote.dto.toUser
import tech.svehla.gratitudejournal.domain.model.User
import timber.log.Timber


class AuthServiceImpl: AuthService {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)
    override val currentUserFlow: Flow<User?> = _currentUserStateFlow.asStateFlow().map { it?.toUser() }

    private val _userChangedSharedFlow = MutableSharedFlow<Unit>(replay = 0)
    override val userChangedFlow: SharedFlow<Unit> = _userChangedSharedFlow

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        firebaseAuth.addAuthStateListener {
            _currentUserStateFlow.tryEmit(it.currentUser)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInAnonymously() {
        firebaseAuth.signInAnonymously().await()
        _userChangedSharedFlow.emit(Unit)
    }

    override suspend fun signInWithGoogle(idToken: String) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        val currentUserId: String? = firebaseAuth.currentUser?.uid
        _userChangedSharedFlow.emit(Unit)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        val currentUserId: String? = firebaseAuth.currentUser?.uid
        _userChangedSharedFlow.emit(Unit)
    }
}