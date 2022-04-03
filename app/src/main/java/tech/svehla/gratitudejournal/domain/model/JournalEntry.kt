package tech.svehla.gratitudejournal.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Immutable
data class JournalEntry(
    val date: String,
    val firstNote: String,
    val secondNote: String,
    val thirdNote: String,
    val imageUrl: String? = null,
    val gifUrl: String? = null,
    val favoriteEntry: Int? = null,
    val dayScore: Int? = null,
    val lastModified: String = LocalDate.now().toString()
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

        fun formatDate(date: String): String = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
            .format(DateTimeFormatter.ofPattern("dd. MMMM yyyy"))
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

    val formattedDate: String = formatDate(date)
}