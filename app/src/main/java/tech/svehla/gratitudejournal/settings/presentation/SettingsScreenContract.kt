package tech.svehla.gratitudejournal.settings.presentation

import tech.svehla.gratitudejournal.settings.domain.model.User

data class SettingsScreenState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val signInErrorMessage: String? = null
) {
    companion object
}
