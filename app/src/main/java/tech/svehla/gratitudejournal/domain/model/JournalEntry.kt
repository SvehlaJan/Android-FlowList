package tech.svehla.gratitudejournal.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class JournalEntry(
  val date: String,
  val firstNote: String,
  val secondNote: String,
  val thirdNote: String,
  val imageUrl: String,
  val gifUrl: String,
  val favoriteEntry: Int,
  val dayScore: Int,
  val lastModified: String
)