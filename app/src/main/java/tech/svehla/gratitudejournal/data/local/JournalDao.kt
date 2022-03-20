package tech.svehla.gratitudejournal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.domain.model.JournalEntry

@Dao
interface JournalDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJournalEntries(journalEntries: List<JournalEntry>)

  @Query("SELECT * FROM JournalEntry WHERE date = :entryDate")
  fun getJournalEntry(entryDate: String): Flow<JournalEntry>

  @Query("SELECT * FROM JournalEntry")
  fun getJournalEntries(): Flow<List<JournalEntry>>
}
