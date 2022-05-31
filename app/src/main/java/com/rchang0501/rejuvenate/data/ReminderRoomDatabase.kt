package com.rchang0501.rejuvenate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Reminder::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReminderRoomDatabase: RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile //ensures changes made to instance from one thread is visible to all other threads immediately
        private var INSTANCE: ReminderRoomDatabase? = null

        // helper function to instantiate/retrieve the database
        fun getDatabase(context: Context): ReminderRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, ReminderRoomDatabase::class.java, "reminder_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}