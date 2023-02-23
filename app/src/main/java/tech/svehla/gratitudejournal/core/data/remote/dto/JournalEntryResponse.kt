package tech.svehla.gratitudejournal.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JournalEntryResponse(
    val entry: JournalEntryDto?,
)
