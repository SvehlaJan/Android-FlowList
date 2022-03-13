package tech.svehla.gratitudejournal.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.svehla.gratitudejournal.model.JournalEntry
import tech.svehla.gratitudejournal.repository.MainRepository
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    fun refresh() {
        TODO("Not yet implemented")
    }

    val journalEntries = mainRepository.getJournalEntries()
}