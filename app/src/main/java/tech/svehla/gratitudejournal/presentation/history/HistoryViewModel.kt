package tech.svehla.gratitudejournal.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.common.Resource
import tech.svehla.gratitudejournal.domain.use_case.history.GetHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<HistoryScreenState> =
        MutableStateFlow(HistoryScreenState())
    val state: StateFlow<HistoryScreenState> = _state

    init {
        loadHistory()
    }

    fun loadHistory() = viewModelScope.launch {
        getHistoryUseCase().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val entries = result.data.sortedByDescending { it.date }
                    _state.update { HistoryScreenState(entries = entries) }
                }
                is Resource.Loading -> {
                    _state.update { HistoryScreenState(isLoading = true) }
                }
                is Resource.Error -> {
                    _state.update { HistoryScreenState(errorReason = result.reason) }
                }
            }
        }
    }
}