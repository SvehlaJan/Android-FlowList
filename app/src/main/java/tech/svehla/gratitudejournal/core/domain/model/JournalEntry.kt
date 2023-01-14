package tech.svehla.gratitudejournal.core.domain.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate

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
    }
}