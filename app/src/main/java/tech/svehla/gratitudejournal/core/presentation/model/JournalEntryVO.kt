package tech.svehla.gratitudejournal.core.presentation.model

import tech.svehla.gratitudejournal.core.presentation.ui.util.DateUtils

data class JournalEntryVO(
    val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val gifUrl: String?,
    val imageUrl: String?,
) {
    fun formattedDate(): String = DateUtils.fromIsoToHumanReadableDate(date)

    companion object
}