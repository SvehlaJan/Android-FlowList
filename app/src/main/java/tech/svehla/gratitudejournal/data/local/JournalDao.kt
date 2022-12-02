package tech.svehla.gratitudejournal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.svehla.gratitudejournal.data.local.entity.JournalEntryEntity

@Dao
interface JournalDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJournalEntries(journalEntries: List<JournalEntryEntity>)

  @Query("SELECT * FROM JournalEntryEntity WHERE date = :entryDate")
  fun getJournalEntryFlow(entryDate: String): Flow<JournalEntryEntity?>

  @Query("SELECT * FROM JournalEntryEntity")
  fun getJournalEntriesFlow(): Flow<List<JournalEntryEntity>>

  @Query("SELECT * FROM JournalEntryEntity WHERE date = :entryDate")
  fun getJournalEntry(entryDate: String): JournalEntryEntity?

  @Query("SELECT * FROM JournalEntryEntity")
  fun getJournalEntries(): List<JournalEntryEntity>

  @Query("DELETE FROM JournalEntryEntity")
  suspend fun deleteAll()
}
