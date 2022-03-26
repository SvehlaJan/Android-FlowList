package tech.svehla.gratitudejournal.presentation.history

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.domain.repository.MainRepository
import tech.svehla.gratitudejournal.domain.use_case.history.GetHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    init {
        getHistory()
    }

    private val _state: MutableState<Resource<List<JournalEntry>>> = mutableStateOf(Resource.Loading())
    val state: State<Resource<List<JournalEntry>>> = _state

    private fun getHistory() {
        getHistoryUseCase().onEach {
            _state.value = it
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        TODO("Not yet implemented")
    }
}