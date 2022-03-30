package com.rchang0501.rejuvenate.remindereditfragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory
import java.util.*

class TimePickerFragment(id: Int) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    val reminderId = id
    lateinit var reminder: Reminder

    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minute = c.get(Calendar.MINUTE)

        if (reminderId > 0) {
            parentFragment?.let {
                viewModel.retrieveReminder(id).observe(it.viewLifecycleOwner) { selectedReminder ->
                    reminder = selectedReminder
                }
            }

            hour = reminder.dueDate.get(Calendar.HOUR_OF_DAY)
            minute = reminder.dueDate.get(Calendar.MINUTE)
        }

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, R.style.TimerPickerDialog, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user

        val newTime = Calendar.getInstance()
        newTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newTime.set(Calendar.MINUTE, minute)


    }
}