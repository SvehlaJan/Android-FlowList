package tech.svehla.gratitudejournal.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.svehla.gratitudejournal.core.data.local.entity.JournalEntryEntity

@Database(entities = [JournalEntryEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

  abstract fun journalDao(): JournalDao
}
