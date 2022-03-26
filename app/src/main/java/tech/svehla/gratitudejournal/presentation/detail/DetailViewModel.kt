package tech.svehla.gratitudejournal.presentation.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.common.Constants
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.use_case.detail.GetDetailUseCase
import tech.svehla.gratitudejournal.domain.use_case.detail.SaveEntryUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        savedStateHandle.get<String>(Constants.PARAM_DATE)?.let { date ->
            getDetail(date)
        }
    }

    private val _state: MutableState<Resource<JournalEntry>> = mutableStateOf(Resource.Loading())
    val state: State<Resource<JournalEntry>> = _state

    fun getDetail(date: String) {
        getDetailUseCase(date).onEach {
            _state.value = it
        }.launchIn(viewModelScope)
    }

    fun saveEntry(newEntry: JournalEntry) {
        viewModelScope.launch {
            saveEntryUseCase(newEntry)
        }
    }

}