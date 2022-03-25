package com.rchang0501.rejuvenate.reminderdetailfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderDetailFragmentBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory

class ReminderDetailFragment: Fragment() {

    lateinit var reminder: Reminder

    private val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    private val navigationArgs: ReminderDetailFragmentArgs by navArgs()

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

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.title = null
        binding.toolbar.setNavigationIcon(R.drawable.ic_navigate_back)

        val id = navigationArgs.itemId

        viewModel.retrieveReminder(id).observe(this.viewLifecycleOwner){ selectedReminder ->
            reminder = selectedReminder
            bind(reminder)
        }
    }

    private fun bind(reminder: Reminder) {
        binding.apply {
            reminderTitle.text = reminder.title
            reminderDate.text = reminder.dueDate
            reminderTime.text = "11:15 PM"
            reminderNotes.text = reminder.notes
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}