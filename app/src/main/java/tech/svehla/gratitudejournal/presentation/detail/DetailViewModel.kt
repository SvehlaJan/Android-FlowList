package tech.svehla.gratitudejournal.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giphy.sdk.core.models.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.di.ApplicationScope
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.use_case.detail.GetDetailUseCase
import tech.svehla.gratitudejournal.domain.use_case.detail.SaveEntryUseCase
import tech.svehla.gratitudejournal.presentation.main.NavScreen
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val formDelegate: DetailScreenFormDelegate,
    @ApplicationScope private val externalScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DetailScreenFormDelegate by formDelegate {

    private val _state: MutableStateFlow<DetailScreenState> = MutableStateFlow(DetailScreenState())
    val state: StateFlow<DetailScreenState> = _state
    private lateinit var _date: String

    init {
        Timber.d("DetailViewModel init")
        savedStateHandle.get<String>(NavScreen.Detail.argument0)?.let { date ->
            _date = date
        }
        loadDetail()
    }

    fun loadDetail(date: String = _date) {
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

    fun onStop() {
        if (formDelegate.hasChanges()) {
            externalScope.launch(Dispatchers.IO) {
                saveEntryUseCase(formDelegate.getNewEntry())
            }
        }
    }
}