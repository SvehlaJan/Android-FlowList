package tech.svehla.gratitudejournal.data.local.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import java.time.LocalDate
import java.util.*

@Entity
@Immutable
data class JournalEntryEntity(
    @PrimaryKey val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val imageUrl: String? = null,
    val gifUrl: String? = null,
    val favoriteEntry: Int? = null,
    val dayScore: Int? = null,
    val lastModified: String = LocalDate.now().toString(),
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

fun JournalEntry.toJournalEntryEntity(): JournalEntryEntity {
    return JournalEntryEntity(
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