package tech.svehla.gratitudejournal.data.remote

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.domain.model.UserEntity

interface AuthService {
    suspend fun signInAnonymously()

    suspend fun signInWithGoogle(idToken: String)

    suspend fun signOut()

    val currentUserId: String?

    val currentUserFlow: Flow<UserEntity?>
}