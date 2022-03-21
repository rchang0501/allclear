package com.rchang0501.rejuvenate.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Reminder) // suspend annotation makes it a coroutine to run on a different thread

    @Update
    suspend fun update(item: Reminder)

    @Delete
    suspend fun delete(item: Reminder)

    @Query("SELECT * from reminder WHERE id = :id")
    fun getReminder(id: Int): Flow<Reminder> // flow is like live data - data will always be updated to be the most recent copy

    @Query("SELECT * from reminder ORDER by dueDate ASC")
    fun getReminders(): Flow<List<Reminder>>
}