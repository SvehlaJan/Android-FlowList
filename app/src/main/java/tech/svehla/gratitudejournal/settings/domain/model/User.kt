package tech.svehla.gratitudejournal.settings.domain.model

data class User(
    val displayName: String?,
    val email: String?,
    val photoUrl: String,
    val id: String,
) {
    companion object
}
