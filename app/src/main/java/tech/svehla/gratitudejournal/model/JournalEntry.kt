package tech.svehla.gratitudejournal.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.*

fun JournalEntry.toFirestoreJournalEntry(): FirestoreJournalEntry {
  return FirestoreJournalEntry(
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

fun FirestoreJournalEntry.toJournalEntry(): JournalEntry {
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

@Entity
@Immutable
data class JournalEntry(
  @PrimaryKey val date: String,
  val firstNote: String,
  val secondNote: String,
  val thirdNote: String,
  val imageUrl: String,
  val gifUrl: String,
  val favoriteEntry: Int,
  val dayScore: Int,
  val lastModified: String
)

data class FirestoreJournalEntry(
  @DocumentId var date: String = "",
  @get: PropertyName("entry_1") @set: PropertyName("entry_1") var firstNote: String? = "",
  @get: PropertyName("entry_2") @set: PropertyName("entry_2") var secondNote: String? = "",
  @get: PropertyName("entry_3") @set: PropertyName("entry_3") var thirdNote: String? = "",
  @get: PropertyName("image_url") @set: PropertyName("image_url") var imageUrl: String? = "",
  @get: PropertyName("gif_url") @set: PropertyName("gif_url") var gifUrl: String? = "",
  @get: PropertyName("favorite_entry") @set: PropertyName("favorite_entry") var favoriteEntry: Int? = 0,
  @get: PropertyName("day_score") @set: PropertyName("day_score") var dayScore: Int? = 0,
  @get: PropertyName("date_modified") @set: PropertyName("date_modified") var lastModified: String? = ""
)