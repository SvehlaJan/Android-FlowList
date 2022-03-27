package tech.svehla.gratitudejournal.data.local.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.svehla.gratitudejournal.domain.model.JournalEntry

@Entity
@Immutable
data class JournalEntryEntity(
    @PrimaryKey val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val imageUrl: String?,
    val gifUrl: String?,
    val favoriteEntry: Int?,
    val dayScore: Int?,
    val lastModified: String
) {
    fun toJournalEntry() = JournalEntry(
        date,
        firstNote,
        secondNote,
        thirdNote,
        imageUrl,
        gifUrl,
        favoriteEntry,
        dayScore,
        lastModified
    )
}

fun JournalEntry.toJournalEntryEntity() =
    JournalEntryEntity(
        date,
        firstNote,
        secondNote,
        thirdNote,
        imageUrl,
        gifUrl,
        favoriteEntry,
        dayScore,
        lastModified
    )