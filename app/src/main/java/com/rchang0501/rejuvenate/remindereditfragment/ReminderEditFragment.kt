package com.rchang0501.rejuvenate.remindereditfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderEditFragmentBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory
import java.util.*

class ReminderEditFragment : Fragment() {

    lateinit var reminder: Reminder
    private var reminderIsCompleted: Boolean = false
    private var reminderDueDate: String = ""

    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    private val navigationArgs: ReminderEditFragmentArgs by navArgs()

    private var _binding: ReminderEditFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReminderEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.reminderId

        binding.toolbarCancelButton.setOnClickListener {
            val action =
                ReminderEditFragmentDirections.actionReminderEditFragmentToReminderDetailFragment(id)
            this.findNavController().navigate(action)
        }

        binding.editReminderTimeButton.setOnClickListener {
            showTimePickerDialog(view)
        }

        if (id > 0) {
            viewModel.retrieveReminder(id).observe(this.viewLifecycleOwner) { selectedReminder ->
                reminder = selectedReminder
                reminderIsCompleted = reminder.isComplete
                bind(reminder)
            }
        } else {
            // add new reminder
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun bind(reminder: Reminder) {
        binding.apply {
            reminderTitle.setText(reminder.title, TextView.BufferType.SPANNABLE)

            // update reminder due date text
            val calendar = Calendar.getInstance()

            // set the selected calendar date as the highlighted date in the calendar view
            calendarView.date = calendar.timeInMillis

            reminderDueDate = viewModel.reminderDueDateText(reminder, calendar)

            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                calendarView.date = calendar.timeInMillis
                reminderDueDate = viewModel.reminderDueDateText(reminder, calendar)
            }


            // show the current time
            editReminderTimeButton.text = viewModel.reminderDueDateTimeText()

            reminderNotes.setText(reminder.notes, TextView.BufferType.SPANNABLE)
            toolbarDoneButton.setOnClickListener {
                updateReminder()
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.reminderTitle.text.toString(),
            binding.reminderNotes.text.toString()
        )
    }

    private fun updateReminder() {
        if (isEntryValid()) {
            viewModel.updateReminder(
                this.navigationArgs.reminderId,
                this.binding.reminderTitle.text.toString(),
                reminderDueDate,
                this.binding.reminderNotes.text.toString(),
                reminderIsCompleted
            )
            this.findNavController().navigateUp()
        }
    }

    private fun showTimePickerDialog(v: View) {
        TimePickerFragment().show(childFragmentManager, "timePicker")
    }
}