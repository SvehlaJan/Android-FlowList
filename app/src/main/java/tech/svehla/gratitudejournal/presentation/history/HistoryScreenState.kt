package tech.svehla.gratitudejournal.presentation.history

import tech.svehla.gratitudejournal.domain.model.ErrorReason
import tech.svehla.gratitudejournal.presentation.model.JournalEntryVO

data class HistoryScreenState(
    val entries: List<JournalEntryVO>? = null,
    val isLoading: Boolean = false,
    val errorReason: ErrorReason? = null
)
