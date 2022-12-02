package tech.svehla.gratitudejournal.presentation.detail

import tech.svehla.gratitudejournal.domain.model.ErrorEntity
import java.util.*

data class DetailScreenState(
    val uiState: UIState = UIState.Loading,
    val signInErrorMessage: String? = null,
    val events: List<UIEvent> = emptyList()
)

sealed class UIState {
    object Content : UIState()
    object Loading : UIState()
    data class Error(val error: ErrorEntity?) : UIState()
    object GifPicker : UIState()
}

sealed class UIEvent(val id: String = UUID.randomUUID().toString()) {
    object NavigateBack : UIEvent()
}