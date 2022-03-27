package tech.svehla.gratitudejournal.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giphy.sdk.core.models.Media
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.use_case.detail.GetDetailUseCase
import tech.svehla.gratitudejournal.domain.use_case.detail.SaveEntryUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.CurrentUserUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignInAnonymouslyUseCase
import tech.svehla.gratitudejournal.domain.use_case.settings.SignInWithGoogleUseCase
import tech.svehla.gratitudejournal.presentation.main.NavScreen
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val formDelegate: DetailScreenFormDelegate,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DetailScreenFormDelegate by formDelegate {

    private lateinit var _date: String
    private val _state: MutableStateFlow<DetailScreenState> = MutableStateFlow(DetailScreenState())
    val state: StateFlow<DetailScreenState> = _state

    init {
        savedStateHandle.get<String>(NavScreen.Detail.argument0)?.let { date ->
            _date = date
            getDetail(date)
        }

        // TODO - find a nicer way
        viewModelScope.launch {
            getDetailUseCase.refreshRequired.collect {
                getDetail(_date)
            }
        }
    }

    fun getDetail(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getDetailUseCase(date).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) { // Delegate has state holders which might've been already observed from UI
                            formDelegate.initValues(result.data ?: JournalEntry.empty(date))
                            _state.value = DetailScreenState(uiState = UIState.Content)
                        }
                    }
                    is Resource.Error -> {
                        _state.value =
                            DetailScreenState(uiState = UIState.Error(errorMessage = result.message))
                    }
                    is Resource.Loading -> {
                        _state.value = DetailScreenState(uiState = UIState.Loading)
                    }
                }
            }
        }
    }

    fun onBackPressed(forceBackPress: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!formDelegate.hasChanges() || forceBackPress) {
                sendEvent(UIEvent.NavigateBack)
                return@launch
            }

            val isSignedIn = currentUserUseCase().stateIn(viewModelScope).value != null
            if (isSignedIn) {
                saveEntryUseCase(formDelegate.getNewEntry())
                sendEvent(UIEvent.NavigateBack)
            } else {
                sendEvent(UIEvent.ShowSignInBottomSheet)
            }

        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            signInWithGoogleUseCase(idToken)
            saveEntryUseCase(formDelegate.getNewEntry())
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            signInAnonymouslyUseCase()
            saveEntryUseCase(formDelegate.getNewEntry())
        }
    }

    fun onGoogleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)
            if (account == null) {
                _state.value = _state.value.copy(signInErrorMessage = "Google sign in failed")
            } else {
                account.idToken?.let {
                    _state.value = _state.value.copy(signInErrorMessage = null)
                    signInWithGoogle(it)
                } ?: run {
                    _state.value =
                        _state.value.copy(signInErrorMessage = "Google sign in failed - no token")
                }
            }
        } catch (e: ApiException) {
            _state.value = _state.value.copy(signInErrorMessage = "Google sign in failed")
        }
    }

    fun onSelectGifClicked() {
        _state.value = DetailScreenState(uiState = UIState.GifPicker)
    }

    fun onGifSelected(media: Media?) {
        media?.images?.original?.gifUrl?.let {
            formDelegate.gifUrl.value = it
        }
        _state.value = DetailScreenState(uiState = UIState.Content)
    }

    private fun sendEvent(event: UIEvent) {
        _state.value = _state.value.copy(events = _state.value.events + event)
    }

    fun onEventConsumed(eventId: String) {
        val events = _state.value.events.filterNot { it.id == eventId }
        _state.value = _state.value.copy(events = events)
    }
}