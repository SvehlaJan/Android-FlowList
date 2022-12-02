package tech.svehla.gratitudejournal.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.di.ApplicationScope
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.use_case.detail.GetDetailUseCase
import tech.svehla.gratitudejournal.domain.use_case.detail.SaveEntryUseCase
import tech.svehla.gratitudejournal.presentation.main.NavScreen
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    @ApplicationScope private val externalScope: CoroutineScope,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<DetailScreenStateNew> =
        MutableStateFlow(DetailScreenStateNew())
    val state: StateFlow<DetailScreenStateNew> = _state
    private lateinit var _date: String
    private var originalUiContent: JournalUiContent? = null

    init {
        savedStateHandle.get<String>(NavScreen.Detail.argument0)?.let { date ->
            _date = date
        }
        loadDetail()
    }

    fun loadDetail(date: String = _date) = viewModelScope.launch {
        getDetailUseCase(date).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val entry = result.data ?: JournalEntry.empty(date)
                    val uiContent = JournalUiContent(
                        date = entry.date,
                        firstNote = entry.firstNote,
                        secondNote = entry.secondNote,
                        thirdNote = entry.thirdNote,
                        imageUrl = entry.imageUrl,
                        gifUrl = entry.gifUrl,
                        dayScore = entry.dayScore,
                        favoriteEntry = entry.favoriteEntry,
                    )
                    originalUiContent = uiContent

                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorReason = null,
                            content = uiContent
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorReason = result.reason
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
                _state.update { it.copy(content = it.content?.copy(firstNote = action.value)) }
            }
            is UIAction.SecondNoteUpdated -> {
                _state.update { it.copy(content = it.content?.copy(secondNote = action.value)) }
            }
            is UIAction.ThirdNoteUpdated -> {
                _state.update { it.copy(content = it.content?.copy(thirdNote = action.value)) }
            }
            UIAction.GifPickerRequested -> {
                _state.update { it.copy(showGifPicker = true) }
            }
            is UIAction.GifSelected -> {
                _state.update {
                    it.copy(
                        showGifPicker = false,
                        content = it.content?.copy(gifUrl = action.url)
                    )
                }
            }
        }
    }

    fun onStop() {
        if (originalUiContent != _state.value.content) {
            externalScope.launch(Dispatchers.IO) {
                val entry = JournalEntry(
                    date = _state.value.content?.date ?: "",
                    firstNote = _state.value.content?.firstNote ?: "",
                    secondNote = _state.value.content?.secondNote ?: "",
                    thirdNote = _state.value.content?.thirdNote ?: "",
                    imageUrl = _state.value.content?.imageUrl,
                    gifUrl = _state.value.content?.gifUrl,
                    dayScore = _state.value.content?.dayScore,
                    favoriteEntry = _state.value.content?.favoriteEntry,
                    lastModified = LocalDate.now().toString(),
                )
                saveEntryUseCase(entry)
            }
        }
    }
}