package tech.svehla.gratitudejournal.core.presentation.model

import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import java.time.LocalDate

fun JournalEntry.toVO() = JournalEntryVO(
    date = date,
    firstNote = firstNote,
    secondNote = secondNote,
    thirdNote = thirdNote,
    gifUrl = gifUrl,
    imageUrl = imageUrl,
)

fun JournalEntryVO.toDomain() = JournalEntry(
    date = date,
    firstNote = firstNote,
    secondNote = secondNote,
    thirdNote = thirdNote,
    gifUrl = gifUrl,
    imageUrl = imageUrl,
    lastModified = LocalDate.now().toString(),
)