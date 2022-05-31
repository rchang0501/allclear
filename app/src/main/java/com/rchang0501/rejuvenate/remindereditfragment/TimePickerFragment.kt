package com.rchang0501.rejuvenate.remindereditfragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    // instatiate view model
    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    // creates a time picker dialogue
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        Log.d("dialogue", "${viewModel.tempReminderDueDateTimeText()}")

        // retrieves the current time/selected time (based on add/edit fragment) from the view model
        val hour = viewModel.getTempReminderDueDateHour()
        val minute = viewModel.getTempReminderDueDateMinute()

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(
            activity,
            R.style.TimerPickerDialog,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }

    // update the set time in view model based on selection
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        viewModel.setTempReminderDueDateHour(hourOfDay)
        viewModel.setTempReminderDueDateMinute(minute)
    }
}