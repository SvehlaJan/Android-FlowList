package tech.svehla.gratitudejournal.presentation.detail

import tech.svehla.gratitudejournal.domain.model.ErrorReason
import java.util.*

data class JournalUiContent(
    val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val gifUrl: String?,
    val dayScore: Int?,
    val favoriteEntry: Int?,
    val imageUrl: String?,
)

data class DetailScreenStateNew(
    val isLoading: Boolean = false,
    val content: JournalUiContent? = null,
    val errorReason: ErrorReason? = null,
    val showGifPicker: Boolean = false,
    val events: List<UIEvent> = emptyList()
)

sealed class UIEvent(val id: String = UUID.randomUUID().toString()) {
    object NavigateBack : UIEvent()
}

sealed class UIAction(val id: String = UUID.randomUUID().toString()) {
    data class FirstNoteUpdated(val value: String) : UIAction()
    data class SecondNoteUpdated(val value: String) : UIAction()
    data class ThirdNoteUpdated(val value: String) : UIAction()
    object GifPickerRequested : UIAction()
    data class GifSelected(val url: String) : UIAction()
}