package tech.svehla.gratitudejournal.detail.presentation

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import java.time.LocalDate
import java.util.*

data class DetailScreenState(
    val isLoading: Boolean = false,
    val content: JournalEntryVO? = null,
    val error: ErrorReason? = null,
    val showGifPicker: Boolean = false,
    val showDatePicker: Boolean = false,
    val events: List<UIEvent> = emptyList()
)

sealed class UIEvent(val id: String = UUID.randomUUID().toString()) {
    data object NavigateBack : UIEvent()
}

sealed class UIAction {
    data object DateClicked : UIAction()
    data class FirstNoteUpdated(val value: String) : UIAction()
    data class SecondNoteUpdated(val value: String) : UIAction()
    data class ThirdNoteUpdated(val value: String) : UIAction()
    data object RefreshData : UIAction()
    data object GifPickerRequested : UIAction()
    data object GifPickerDismissed : UIAction()
    data class GifSelected(val url: String) : UIAction()
    data class DateSelected(val date: LocalDate?) : UIAction()
}
