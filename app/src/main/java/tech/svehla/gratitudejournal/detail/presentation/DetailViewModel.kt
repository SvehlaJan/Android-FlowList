package tech.svehla.gratitudejournal.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.core.data.remote.util.Resource
import tech.svehla.gratitudejournal.core.di.ApplicationScope
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import tech.svehla.gratitudejournal.core.presentation.NavRoute
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.core.presentation.model.toDomain
import tech.svehla.gratitudejournal.core.presentation.model.toVO
import tech.svehla.gratitudejournal.detail.domain.GetDetailUseCase
import tech.svehla.gratitudejournal.detail.domain.SaveEntryUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    @ApplicationScope private val externalScope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state: MutableStateFlow<DetailScreenState> = MutableStateFlow(DetailScreenState())
    val state: StateFlow<DetailScreenState> = _state
    private lateinit var _date: String
    private var initialJournalEntry: JournalEntryVO? = null

    init {
        savedStateHandle.get<String>(NavRoute.Detail.argument0)?.let { date ->
            _date = date
        }
        loadDetail()
    }

    private fun loadDetail(date: String = _date) = viewModelScope.launch {
        getDetailUseCase(date).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val entry = result.data ?: JournalEntry.empty(date)
                    val vo = entry.toVO()
                    initialJournalEntry = vo

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            content = vo,
                        )
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error,
                        )
                    }
                }

                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendEvent(event: UIEvent) {
        _state.value = _state.value.copy(events = _state.value.events + event)
    }

    fun onEventConsumed(eventId: String) {
        val events = _state.value.events.filterNot { it.id == eventId }
        _state.value = _state.value.copy(events = events)
    }

    fun onUiAction(action: UIAction) {
        when (action) {
            is UIAction.FirstNoteUpdated -> {
                val newContent = _state.value.content?.copy(firstNote = action.value)
                _state.update { it.copy(content = newContent) }
            }

            is UIAction.SecondNoteUpdated -> {
                val newContent = _state.value.content?.copy(secondNote = action.value)
                _state.update { it.copy(content = newContent) }
            }

            is UIAction.ThirdNoteUpdated -> {
                val newContent = _state.value.content?.copy(thirdNote = action.value)
                _state.update { it.copy(content = newContent) }
            }

            is UIAction.GifPickerRequested -> {
                _state.update { it.copy(showGifPicker = true) }
            }

            is UIAction.GifPickerDismissed -> {
                _state.update { it.copy(showGifPicker = false) }
            }

            is UIAction.GifSelected -> {
                val newContent = _state.value.content?.copy(gifUrl = action.url)
                _state.update {
                    it.copy(
                        showGifPicker = false,
                        content = newContent,
                    )
                }
            }

            UIAction.RefreshData -> {
                loadDetail()
            }
        }
    }

    fun onStop() {
        if (initialJournalEntry?.equals(_state.value.content) == false) {
            externalScope.launch {
                _state.value.content?.toDomain()?.let {
                    saveEntryUseCase(it)
                }
            }
        }
    }
}