package tech.svehla.gratitudejournal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.svehla.gratitudejournal.domain.model.JournalEntry

@Database(entities = [JournalEntry::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

  abstract fun posterDao(): JournalDao
}
