package tech.svehla.gratitudejournal.presentation.settings

import tech.svehla.gratitudejournal.domain.model.UserEntity

data class SettingsScreenState(
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val signInErrorMessage: String? = null
) {
    companion object
}
