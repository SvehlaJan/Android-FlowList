package tech.svehla.gratitudejournal.core.data.remote.dto

import kotlinx.serialization.Serializable
import tech.svehla.gratitudejournal.core.data.local.entity.JournalEntryEntity
import tech.svehla.gratitudejournal.core.domain.model.JournalEntry
import java.time.LocalDate


@Serializable
data class JournalEntryDto(
    val date: String,
    val firstNote: String? = "",
    val secondNote: String? = "",
    val thirdNote: String? = "",
    val imageUrl: String? = "",
    val gifUrl: String? = "",
    val favoriteEntry: Int? = 0,
    val dayScore: Int? = 0,
    val lastModified: String? = "",
) {
    fun toJournalEntry(): JournalEntry {
        return JournalEntry(
            date = date,
            firstNote = firstNote ?: "",
            secondNote = secondNote ?: "",
            thirdNote = thirdNote ?: "",
            imageUrl = imageUrl ?: "",
            gifUrl = gifUrl ?: "",
            favoriteEntry = favoriteEntry ?: -1,
            dayScore = dayScore ?: -1,
            lastModified = lastModified ?: LocalDate.now().toString(),
        )
    }

    fun toJournalEntryEntity(): JournalEntryEntity {
        return JournalEntryEntity(
            date = date,
            firstNote = firstNote ?: "",
            secondNote = secondNote ?: "",
            thirdNote = thirdNote ?: "",
            imageUrl = imageUrl ?: "",
            gifUrl = gifUrl ?: "",
            favoriteEntry = favoriteEntry ?: -1,
            dayScore = dayScore ?: -1,
            lastModified = lastModified ?: LocalDate.now().toString(),
        )
    }
}

fun JournalEntry.toJournalEntryDto(): JournalEntryDto {
    return JournalEntryDto(
        date = date,
        firstNote = firstNote,
        secondNote = secondNote,
        thirdNote = thirdNote,
        imageUrl = imageUrl,
        gifUrl = gifUrl,
        favoriteEntry = favoriteEntry,
        dayScore = dayScore,
        lastModified = lastModified,
    )
}