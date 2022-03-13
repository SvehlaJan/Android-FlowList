package tech.svehla.gratitudejournal.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.network.AuthService
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    val currentUser = authService.currentUserFlow

    val userLoggedIn = authService.isLoggedInFlow

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            authService.signInWithGoogle(idToken)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authService.signOut()
        }
    }
}