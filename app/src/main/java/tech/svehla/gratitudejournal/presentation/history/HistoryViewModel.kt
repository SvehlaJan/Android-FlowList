package tech.svehla.gratitudejournal.presentation.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.svehla.gratitudejournal.data.repository.MainRepository
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