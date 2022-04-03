package com.rchang0501.rejuvenate.reminderlistfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderListFragmentBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory


class ReminderListFragment : Fragment() {

    val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    private var filteredList: List<Reminder>? = null
    private var currentList: List<Reminder>? = null

    private var _binding: ReminderListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ReminderListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentList = viewModel.allReminders.value

        binding.toolbarAddButton.setOnClickListener {
            val action =
                ReminderListFragmentDirections.actionReminderListFragmentToReminderEditFragment()
            this.findNavController().navigate(action)
        }

        binding.segmentedControlGroup.setOnSelectedOptionChangeCallback { selectedIndex ->
            if (selectedIndex == 0) {
                viewModel.setReminderFilterMode(ReminderFilterMode.TODAY)
            } else if (selectedIndex == 1) {
                viewModel.setReminderFilterMode(ReminderFilterMode.FUTURE)
            } else {
                viewModel.setReminderFilterMode(ReminderFilterMode.ALL)
            }
        }

        val adapter = ReminderListAdapter(viewModel) {
            val action =
                ReminderListFragmentDirections.actionReminderListFragmentToReminderDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        viewModel.allReminders.observe(this.viewLifecycleOwner) { reminders ->
            currentList = reminders
            updateList(adapter)
        }

        viewModel.reminderFilterMode.observe(this.viewLifecycleOwner) {
            binding.segmentedControlGroup.setSelectedIndex(
                viewModel.getReminderFilterModePosition(),
                false
            )
            updateList(adapter)
        }

        // Swipe handler to delete items from the list
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                viewModel.deleteReminder(viewModel.allReminders.value!![position])
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun updateList(adapter: ReminderListAdapter) {
        if (viewModel.reminderFilterMode.value == ReminderFilterMode.TODAY) {
            filteredList = currentList?.filter {
                viewModel.dueToday(it.dueDate)
            }
        } else if (viewModel.reminderFilterMode.value == ReminderFilterMode.FUTURE) {
            filteredList = currentList?.filter {
                viewModel.dueFuture(it.dueDate)
            }
        } else {
            filteredList = currentList
        }
        adapter.submitList(filteredList)
        binding.recyclerView.smoothScrollToPosition(0)
    }
}