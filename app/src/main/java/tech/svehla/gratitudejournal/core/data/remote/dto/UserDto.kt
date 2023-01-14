package tech.svehla.gratitudejournal.core.data.remote.dto

import com.google.firebase.auth.FirebaseUser
import tech.svehla.gratitudejournal.settings.domain.model.User

// class UserDto() : FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        displayName,
        email,
        photoUrl.toString(),
        uid
    )
}