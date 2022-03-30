package com.rchang0501.rejuvenate.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.data.ReminderDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class RejuvenateViewModel(private val reminderDao: ReminderDao) : ViewModel() {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getReminders().asLiveData()

    fun isEntryValid(reminderTitle: String): Boolean {
        if (reminderTitle.isBlank()) {
            return false
        }
        return true
    }

    fun addNewReminder(reminderTitle: String, reminderDueDate: Calendar, reminderNotes: String) {
        val newReminder = getNewReminderEntry(
            reminderTitle,
            reminderDueDate,
            reminderNotes
        ) // converts the item to correct value types
        insertReminder(newReminder) // adds the new item to the database
    }

    fun retrieveReminder(id: Int): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.delete(reminder)
        }
    }

    // --- VARIOUS UPDATES TO THE DATA

    /*
    fun sellItem(item: Item){
        if (item.quantityInStock > 0){
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }*/

    // --------------------------------

    fun changeCompleted(reminder: Reminder) {
        Log.d("Rejuvenate View Model", "changeCompleted function called")

        if (!reminder.isComplete) {
            val newReminder = reminder.copy(isComplete = true)
            updateReminder(newReminder)
        } else if (reminder.isComplete) {
            val newReminder = reminder.copy(isComplete = false)
            updateReminder(newReminder)
        }
    }

    fun updateReminder(
        reminderId: Int,
        reminderTitle: String,
        reminderDueDate: Calendar,
        reminderNotes: String,
        reminderIsComplete: Boolean
    ) {
        val updatedReminder = getUpdatedReminderEntry(
            reminderId,
            reminderTitle,
            reminderDueDate,
            reminderNotes,
            reminderIsComplete
        )
        updateReminder(updatedReminder)
    }

    private fun getUpdatedReminderEntry(
        reminderId: Int,
        reminderTitle: String,
        reminderDueDate: Calendar,
        reminderNotes: String,
        reminderIsComplete: Boolean
    ): Reminder {
        return Reminder(
            id = reminderId,
            title = reminderTitle,
            dueDate = reminderDueDate,
            notes = reminderNotes,
            isComplete = reminderIsComplete
        )
    }

    private fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.update(reminder)
        }
    }

    private fun getNewReminderEntry(
        reminderTitle: String,
        reminderDueDate: Calendar,
        reminderNotes: String?
    ): Reminder {
        return Reminder(
            title = reminderTitle,
            dueDate = reminderDueDate,
            notes = reminderNotes,
        )
    }

    private fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insert(reminder)
        }
    }

    fun reminderDueDateText(reminder: Reminder): String {
        val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())

        if (dueToday(reminder.dueDate)) {
            return "Today"
        } else {
            return dateFormatter.format(reminder.dueDate.time)
        }
    }

    private fun dueToday(selectedDate: Calendar): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
    }

    fun reminderDueDateTimeText(reminder: Reminder): String {
        val timeFormatter = SimpleDateFormat("h:mm a")
        return timeFormatter.format(reminder.dueDate.time)
    }

    fun reminderDueDateWithTimeText(reminder: Reminder): String {
        return reminderDueDateText(reminder) + " at " + reminderDueDateTimeText(reminder)
    }
}

// instantiates the view model instance
class RejuvenateViewModelFactory(private val reminderDao: ReminderDao) : ViewModelProvider.Factory {

    // takes any class type as an argument and returns a view model object
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // check if the modelClass is the same as the InventoryViewModel class and return an instance of it
        // otherwise, throw an exception
        if (modelClass.isAssignableFrom(RejuvenateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RejuvenateViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}