package tech.svehla.gratitudejournal.data.remote.dto

import com.google.firebase.auth.FirebaseUser
import tech.svehla.gratitudejournal.domain.model.UserEntity

// class UserDto() : FirebaseUser

fun FirebaseUser.toUser(): UserEntity {
    return UserEntity(
        displayName,
        email,
        photoUrl.toString(),
        uid
    )
}