package com.rchang0501.rejuvenate.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.data.ReminderDao
import com.rchang0501.rejuvenate.reminderlistfragment.ReminderFilterMode
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class RejuvenateViewModel(private val reminderDao: ReminderDao) : ViewModel() {
    // list of all reminders in the database
    val allReminders: LiveData<List<Reminder>> = reminderDao.getReminders().asLiveData()

    // state for the filter mode in home screen
    private val _reminderFilterMode = MutableLiveData<ReminderFilterMode>(ReminderFilterMode.TODAY)
    val reminderFilterMode = _reminderFilterMode

    // temporary reminder due date and time during reminder time changes
    private val _tempReminderDueDate = MutableLiveData<Calendar>(Calendar.getInstance())
    private val _tempReminderDueDateTime: MutableLiveData<Long> = MutableLiveData<Long>()
    val tempReminderDueDateTime: LiveData<Long> = _tempReminderDueDateTime

    // getter and setter methods for the current reminder filter mode
    fun setReminderFilterMode(filterMode: ReminderFilterMode) {
        _reminderFilterMode.value = filterMode
    }
    fun getReminderFilterModePosition(): Int {
        return _reminderFilterMode.value?.position ?: 0
    }

    // below are getter and setter methods for year, month, date, hour, minute props
    // used when updating reminder due dates and times
    private fun updateTempReminderDueDateTime() {
        _tempReminderDueDateTime.value = _tempReminderDueDate.value!!.timeInMillis
    }

    fun setTempReminderDueDate(newDate: Calendar) {
        _tempReminderDueDate.value = newDate
        updateTempReminderDueDateTime()
    }

    fun setTempReminderDueDateYear(year: Int) {
        _tempReminderDueDate.value?.set(Calendar.YEAR, year)
        updateTempReminderDueDateTime()
    }

    fun setTempReminderDueDateMonth(month: Int) {
        _tempReminderDueDate.value?.set(Calendar.MONTH, month)
        updateTempReminderDueDateTime()
    }

    fun setTempReminderDueDateDay(day: Int) {
        _tempReminderDueDate.value?.set(Calendar.DAY_OF_MONTH, day)
        updateTempReminderDueDateTime()
    }

    fun setTempReminderDueDateHour(hourOfDay: Int) {
        _tempReminderDueDate.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        updateTempReminderDueDateTime()
    }

    fun setTempReminderDueDateMinute(minute: Int) {
        _tempReminderDueDate.value?.set(Calendar.MINUTE, minute)
        updateTempReminderDueDateTime()
    }

    fun getTempReminderDueDate(): Calendar {
        return _tempReminderDueDate.value!!
    }

    fun getTempReminderDueDateHour(): Int {
        return _tempReminderDueDate.value!!.get(Calendar.HOUR_OF_DAY)
    }

    fun getTempReminderDueDateMinute(): Int {
        return _tempReminderDueDate.value!!.get(Calendar.MINUTE)
    }

    // check reminder
    fun isEntryValid(reminderTitle: String): Boolean {
        if (reminderTitle.isBlank()) {
            return false
        }
        return true
    }

    // get reminder
    fun retrieveReminder(id: Int): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    // create new reminder to be added
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

    // callable function to add new reminder
    fun addNewReminder(reminderTitle: String, reminderDueDate: Calendar, reminderNotes: String) {
        val newReminder = getNewReminderEntry(
            reminderTitle,
            reminderDueDate,
            reminderNotes
        ) // converts the item to correct value types
        insertReminder(newReminder) // adds the new item to the database
    }

    // add new reminder to database
    private fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insert(reminder)
        }
    }

    // create reminder object for update
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

    // callable function to update reminder
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

    // update reminder in database
    private fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.update(reminder)
        }
    }

    // remove reminder in database
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.delete(reminder)
        }
    }

    // callable function to change the completion status of reminder
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

    // helper function to determine if the reminder is due today
    fun dueToday(selectedDate: Calendar): Boolean {
        val today = Calendar.getInstance()
        return (today.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH) &&
                today.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR))
    }

    // helper function to determine if the reminder is due in the future
    fun dueFuture(selectedDate: Calendar): Boolean {
        val today = Calendar.getInstance()
        return selectedDate.timeInMillis > today.timeInMillis && !dueToday(selectedDate)
    }

    // date formatter with month and date
    private fun reminderDueDateText(reminder: Reminder): String {
        val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())

        return if (dueToday(reminder.dueDate)) {
            "Today"
        } else {
            dateFormatter.format(reminder.dueDate.time)
        }
    }

    // date formatter with weekday used in detail view
    fun reminderDueDateTextForDetail(reminder: Reminder): String {
        return if (dueToday(reminder.dueDate)) {
            "Today"
        } else {
            reminderDueDateWithWeekdayText(reminder)
        }
    }

    // time formatter with hour, minute, and AM/PM
    @SuppressLint("SimpleDateFormat")
    fun reminderDueDateTimeText(reminder: Reminder): String {
        val timeFormatter = SimpleDateFormat("h:mm a")
        return timeFormatter.format(reminder.dueDate.time)
    }

    // date and time formatter that combines date and time formatted strings
    fun reminderDueDateWithTimeText(reminder: Reminder): String {
        return reminderDueDateText(reminder) + " at " + reminderDueDateTimeText(reminder)
    }

    // date formatter with full weekday text
    private fun reminderDueDateWithWeekdayText(reminder: Reminder): String {
        val timeFormatter = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        return timeFormatter.format(reminder.dueDate.time)
    }

    // time formatter for temp reminder time stored during time changes
    @SuppressLint("SimpleDateFormat")
    fun tempReminderDueDateTimeText(): String {
        val timeFormatter = SimpleDateFormat("h:mm a")
        return timeFormatter.format(_tempReminderDueDate.value!!.time)
    }
}

// instantiates the view model
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