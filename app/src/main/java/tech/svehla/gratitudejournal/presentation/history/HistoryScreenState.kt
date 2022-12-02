package tech.svehla.gratitudejournal.presentation.history

import tech.svehla.gratitudejournal.domain.model.ErrorEntity
import tech.svehla.gratitudejournal.domain.model.JournalEntry

data class HistoryScreenState(
    val entries: List<JournalEntry>? = null,
    val isLoading: Boolean = false,
    val error: ErrorEntity? = null
)
