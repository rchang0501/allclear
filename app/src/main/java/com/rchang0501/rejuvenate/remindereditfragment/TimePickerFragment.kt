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

    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        Log.d("dialogue", "${viewModel.tempReminderDueDateTimeText()}")

        val hour = viewModel.getTempReminderDueDateHour()
        val minute = viewModel.getTempReminderDueDateMinute()

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, R.style.TimerPickerDialog, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user

        viewModel.setTempReminderDueDateHour(hourOfDay)
        viewModel.setTempReminderDueDateMinute(minute)
    }
}