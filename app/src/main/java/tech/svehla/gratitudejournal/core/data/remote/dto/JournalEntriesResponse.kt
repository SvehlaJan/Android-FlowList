package tech.svehla.gratitudejournal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntriesResponse(
    val entries: List<JournalEntryDto>
)
