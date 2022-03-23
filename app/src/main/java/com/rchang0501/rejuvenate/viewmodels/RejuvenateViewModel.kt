package com.rchang0501.rejuvenate.viewmodels

import androidx.lifecycle.*
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.data.ReminderDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RejuvenateViewModel(private val reminderDao: ReminderDao): ViewModel() {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getReminders().asLiveData()

    fun isEntryValid(reminderTitle: String, reminderDueDate: String): Boolean {
        if (reminderTitle.isBlank() || reminderDueDate.isBlank()) {
            return false
        }
        return true
    }

    fun addNewItem(reminderTitle: String, reminderDueDate: String, reminderNotes: String){
        val newReminder = getNewReminderEntry(reminderTitle, reminderDueDate, reminderNotes) // converts the item to correct value types
        insertReminder(newReminder) // adds the new item to the database
    }

    fun retrieveReminder(id: Int): LiveData<Reminder>{
        return reminderDao.getReminder(id).asLiveData()
    }

    fun deleteReminder(reminder: Reminder){
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
        if (reminder.isComplete == false) {
            reminder.isComplete = true
            updateReminder(reminder)
        } else if (reminder.isComplete == true) {
            reminder.isComplete = false
            updateReminder(reminder)
        }
    }

    fun updateReminder(
        reminderId: Int,
        reminderTitle: String,
        reminderDueDate: String,
        reminderNotes: String,
        reminderIsComplete: Boolean
    ){
        val updatedReminder = getUpdatedReminderEntry(reminderId, reminderTitle, reminderDueDate, reminderNotes, reminderIsComplete)
        updateReminder(updatedReminder)
    }

    private fun getUpdatedReminderEntry(
        reminderId: Int,
        reminderTitle: String,
        reminderDueDate: String,
        reminderNotes: String,
        reminderIsComplete: Boolean
    ) : Reminder {
        return Reminder(
            id = reminderId,
            title = reminderTitle,
            dueDate = reminderDueDate,
            notes = reminderNotes,
            isComplete = reminderIsComplete
        )
    }

    private fun updateReminder(reminder: Reminder){
        viewModelScope.launch {
            reminderDao.update(reminder)
        }
    }

    private fun getNewReminderEntry(reminderTitle: String, reminderDueDate: String, reminderNotes: String?): Reminder {
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