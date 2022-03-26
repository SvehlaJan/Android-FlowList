package tech.svehla.gratitudejournal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.svehla.gratitudejournal.data.local.entity.JournalEntryEntity

@Database(entities = [JournalEntryEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

  abstract fun journalDao(): JournalDao
}
