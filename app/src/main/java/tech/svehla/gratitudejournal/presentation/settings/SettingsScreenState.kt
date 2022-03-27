package tech.svehla.gratitudejournal.presentation.settings

import tech.svehla.gratitudejournal.domain.model.User

data class SettingsScreenState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val signInErrorMessage: String? = null
)
