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

        binding.editReminderTimeButton.setOnClickListener {
            showTimePickerDialog()
        }

        if (id > 0) {
            binding.toolbarCancelButton.setOnClickListener {
                val action =
                    ReminderEditFragmentDirections.actionReminderEditFragmentToReminderDetailFragment(
                        id
                    )
                this.findNavController().navigate(action)
            }

            // the observer controls what happens when the live data changes
            // in this case we re-assign all values and then bind said values to the ui
            viewModel.retrieveReminder(id).observe(this.viewLifecycleOwner) { selectedReminder ->
                reminder = selectedReminder
                reminderIsCompleted = reminder.isComplete
                viewModel.setTempReminderDueDate(reminder.dueDate)
                bind(reminder)
            }
        } else {
            binding.toolbarCancelButton.setOnClickListener {
                val action =
                    ReminderEditFragmentDirections.actionReminderEditFragmentToReminderListFragment()
                this.findNavController().navigate(action)
            }
            binding.toolbarDoneButton.setOnClickListener {
                addNewReminder()
            }
        }

        viewModel.setTempReminderDueDate(Calendar.getInstance())
        viewModel.tempReminderDueDateTime.observe(this.viewLifecycleOwner) {
            binding.apply {
                editReminderTimeButton.text = viewModel.tempReminderDueDateTimeText()
            }
        }

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            viewModel.setTempReminderDueDateYear(year)
            viewModel.setTempReminderDueDateMonth(month)
            viewModel.setTempReminderDueDateDay(dayOfMonth)
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
            calendarView.date = reminder.dueDate.timeInMillis
            reminderNotes.setText(reminder.notes, TextView.BufferType.SPANNABLE)
            toolbarDoneButton.setOnClickListener {
                updateReminder()
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.reminderTitle.text.toString(),
        )
    }

    private fun updateReminder() {
        if (isEntryValid()) {
            viewModel.updateReminder(
                this.navigationArgs.reminderId,
                this.binding.reminderTitle.text.toString(),
                viewModel.getTempReminderDueDate(),
                this.binding.reminderNotes.text.toString(),
                reminderIsCompleted
            )
            this.findNavController().navigateUp()
        }
    }

    private fun addNewReminder() {
        if (isEntryValid()) {
            viewModel.addNewReminder(
                binding.reminderTitle.text.toString(),
                viewModel.getTempReminderDueDate(),
                binding.reminderNotes.text.toString()
            )
            val action =
                ReminderEditFragmentDirections.actionReminderEditFragmentToReminderListFragment()
            findNavController().navigate(action)
        }
    }

    private fun showTimePickerDialog() {
        TimePickerFragment().show(this.childFragmentManager, "timePicker")
    }
}