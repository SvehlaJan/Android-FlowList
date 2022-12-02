package tech.svehla.gratitudejournal.domain.model

data class UserEntity(
    val displayName: String?,
    val email: String?,
    val photoUrl: String,
    val id: String,
) {
    companion object
}
