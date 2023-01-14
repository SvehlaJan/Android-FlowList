package tech.svehla.gratitudejournal.core.data.remote

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.settings.domain.model.User

interface AuthService {
    suspend fun signInAnonymously()

    suspend fun signInWithGoogle(idToken: String)

    suspend fun signOut()

    val currentUserId: String?

    val currentUserFlow: Flow<User?>
}