package tech.svehla.gratitudejournal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.domain.use_case.settings.CurrentUserUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignInWithGoogleUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignOutUseCase
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    val currentUser = currentUserUseCase()

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken)
        }
    }

    fun signOut() {
        signOutUseCase()
    }
}