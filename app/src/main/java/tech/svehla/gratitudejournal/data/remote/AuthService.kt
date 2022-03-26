package tech.svehla.gratitudejournal.data.remote

import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.domain.model.User

interface AuthService {
    suspend fun signInWithGoogle(idToken: String)

    fun signOut()

    val currentUserId: String

    val currentUserFlow: Flow<User?>
}