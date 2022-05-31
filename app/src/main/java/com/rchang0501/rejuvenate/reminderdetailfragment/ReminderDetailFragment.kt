package com.rchang0501.rejuvenate.reminderdetailfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderDetailFragmentBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory

class ReminderDetailFragment : Fragment() {

    // current reminder being referenced
    lateinit var reminder: Reminder

    // instantiate view model
    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    // navigation argument from nav_graph that contains the reminder id we are referencing
    private val navigationArgs: ReminderDetailFragmentArgs by navArgs()

    // view binding
    private var _binding: ReminderDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReminderDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // reminder id from nav args
        val id = navigationArgs.reminderId

        // bind edit button and navigate to edit fragment
        binding.toolbarEditButton.setOnClickListener {
            val action =
                ReminderDetailFragmentDirections.actionReminderDetailFragmentToReminderEditFragment(
                    id
                )
            this.findNavController().navigate(action)
        }

        // pop up to list fragment
        binding.toolbarBackButton.setOnClickListener {
            this.findNavController().navigateUp()
        }

        // observe changes to the selected reminder and update the ui
        viewModel.retrieveReminder(id).observe(this.viewLifecycleOwner) { selectedReminder ->
            reminder = selectedReminder
            bind(reminder)
        }
    }

    // helper function to update the ui
    private fun bind(reminder: Reminder) {
        binding.apply {
            reminderTitle.text = reminder.title
            reminderDate.text = viewModel.reminderDueDateTextForDetail(reminder)
            reminderTime.text = viewModel.reminderDueDateTimeText(reminder)
            reminderNotes.text = reminder.notes
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}