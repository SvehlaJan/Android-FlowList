package tech.svehla.gratitudejournal.presentation.history

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.use_case.history.GetHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val _state: MutableState<Resource<List<JournalEntry>>> =
        mutableStateOf(Resource.Loading())
    val state: State<Resource<List<JournalEntry>>> = _state

    init {
        getHistory()

        // TODO - find a nicer way
        viewModelScope.launch {
            getHistoryUseCase.refreshRequired.collectLatest {
                getHistory()
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            getHistoryUseCase().collect {
                _state.value = it
            }
        }
    }

    fun refresh() {
        getHistory()
    }
}