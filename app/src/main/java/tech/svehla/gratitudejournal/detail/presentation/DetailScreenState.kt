package tech.svehla.gratitudejournal.detail.presentation

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import java.util.*

data class DetailScreenStateNew(
    val isLoading: Boolean = false,
    val content: JournalEntryVO? = null,
    val errorReason: ErrorReason? = null,
    val showGifPicker: Boolean = false,
    val events: List<UIEvent> = emptyList()
)

sealed class UIEvent(val id: String = UUID.randomUUID().toString()) {
    object NavigateBack : UIEvent()
}

sealed class UIAction {
    data class FirstNoteUpdated(val value: String) : UIAction()
    data class SecondNoteUpdated(val value: String) : UIAction()
    data class ThirdNoteUpdated(val value: String) : UIAction()
    object GifPickerRequested : UIAction()
    data class GifSelected(val url: String) : UIAction()
}