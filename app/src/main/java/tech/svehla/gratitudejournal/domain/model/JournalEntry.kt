package tech.svehla.gratitudejournal.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class JournalEntry(
    val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val imageUrl: String?,
    val gifUrl: String?,
    val favoriteEntry: Int?,
    val dayScore: Int?,
    val lastModified: String
) {
    companion object {
        fun empty(date: String) = JournalEntry(
            date = date,
            firstNote = "",
            secondNote = "",
            thirdNote = "",
            imageUrl = null,
            gifUrl = null,
            favoriteEntry = null,
            dayScore = null,
            lastModified = LocalDate.now().toString()
        )
    }

    fun isContentEqual(other: JournalEntry): Boolean {
        return date == other.date &&
                firstNote == other.firstNote &&
                secondNote == other.secondNote &&
                thirdNote == other.thirdNote &&
                imageUrl == other.imageUrl &&
                gifUrl == other.gifUrl &&
                favoriteEntry == other.favoriteEntry &&
                dayScore == other.dayScore &&
                lastModified == other.lastModified
    }
}