package tech.svehla.gratitudejournal.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.svehla.gratitudejournal.model.JournalEntry
import tech.svehla.gratitudejournal.persistence.JournalDao

@Database(entities = [JournalEntry::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

  abstract fun posterDao(): JournalDao
}
