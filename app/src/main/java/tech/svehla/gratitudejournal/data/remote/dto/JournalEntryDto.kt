package tech.svehla.gratitudejournal.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import tech.svehla.gratitudejournal.data.local.entity.JournalEntryEntity
import tech.svehla.gratitudejournal.domain.model.JournalEntry
import java.util.*


data class JournalEntryDto(
    @DocumentId var date: String = "",
    @get: PropertyName("entry_1") @set: PropertyName("entry_1") var firstNote: String? = "",
    @get: PropertyName("entry_2") @set: PropertyName("entry_2") var secondNote: String? = "",
    @get: PropertyName("entry_3") @set: PropertyName("entry_3") var thirdNote: String? = "",
    @get: PropertyName("image_url") @set: PropertyName("image_url") var imageUrl: String? = "",
    @get: PropertyName("gif_url") @set: PropertyName("gif_url") var gifUrl: String? = "",
    @get: PropertyName("favorite_entry") @set: PropertyName("favorite_entry") var favoriteEntry: Int? = 0,
    @get: PropertyName("day_score") @set: PropertyName("day_score") var dayScore: Int? = 0,
    @get: PropertyName("date_modified") @set: PropertyName("date_modified") var lastModified: String? = ""
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
            lastModified = lastModified ?: Date().toString()
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
            lastModified = lastModified ?: Date().toString()
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
        lastModified = lastModified
    )
}