package tech.svehla.gratitudejournal.history.presentation

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO

data class HistoryScreenState(
    val entries: List<JournalEntryVO>? = null,
    val isLoading: Boolean = false,
    val errorReason: ErrorReason? = null
)
