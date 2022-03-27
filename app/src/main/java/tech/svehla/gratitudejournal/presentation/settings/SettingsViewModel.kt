package tech.svehla.gratitudejournal.presentation.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.domain.use_case.settings.CurrentUserUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignInWithGoogleUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignOutUseCase
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {

    private val _state: MutableState<SettingsScreenState> = mutableStateOf(SettingsScreenState())
    val state: State<SettingsScreenState> = _state

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            currentUserUseCase().collect {
                _state.value = SettingsScreenState(currentUser = it)
            }
        }
    }

    fun onGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)
            if (account == null) {
                setSignInErrorMessage("Google sign in failed")
            } else {
                account.idToken?.let {
                    signInWithGoogle(it)
                } ?: run {
                    setSignInErrorMessage("Google sign in failed - no token")
                }
            }
        } catch (e: ApiException) {
            setSignInErrorMessage("Google sign in failed")
        }
    }


    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken)
            setSignInErrorMessage(null)
        }
    }

    private fun setSignInErrorMessage(message: String?) {
        _state.value = _state.value.copy(signInErrorMessage = message, isLoading = false)
    }

    fun onSignInClicked() {
        _state.value = SettingsScreenState(isLoading = true)
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}