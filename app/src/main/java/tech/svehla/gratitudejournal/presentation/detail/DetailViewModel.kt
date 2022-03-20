package tech.svehla.gratitudejournal.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import tech.svehla.gratitudejournal.data.repository.MainRepository
import tech.svehla.gratitudejournal.common.Resource
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val dateFlow = MutableSharedFlow<String>(replay = 1)
    private val dateChannel = Channel<String?>()

//    val journalEntry = dateChannel.receiveAsFlow()
//        .filterNotNull()
//        .mapLatest { date ->
//            mainRepository.getJournalEntry(date)
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(),
//            initialValue = ""
//        )

    val journalEntry: Flow<Resource<JournalEntry>> = dateFlow.flatMapLatest {
        mainRepository.getJournalEntry(it)
    }
//    val journalEntry = mainRepository.getJournalEntry("2020-01-01")

    fun loadDetail(date: String) {
        viewModelScope.launch {
//            dateChannel.send(date)
            dateFlow.emit(date)
        }
    }

    fun saveEntry(newEntry: JournalEntry) {
        viewModelScope.launch {
            mainRepository.saveJournalEntry(newEntry)
        }
    }

    fun navigateBack() {
//        viewModelScope.launch {
//            dateFlow.emit("")
//        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}