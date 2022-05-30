package com.rchang0501.rejuvenate.reminderlistfragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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

    // instantiate view model
    val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    // track the filtered list and all items in the current list
    private var filteredList: List<Reminder>? = null
    private var currentList: List<Reminder>? = null

    // track number of items selected for progress animations
    private var progress: Int = 0
    private var oldProgress: Int = 0

    // view binding to layout
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the current list
        currentList = viewModel.allReminders.value

        // set the new progress view's max value based othe current list
        binding.apply {
            progressView.max = currentList?.size ?: 100
            progressView.progress = currentList?.filter {
                it.isComplete
            }?.size ?: 0
        }

        // bind the + button in home screen
        binding.toolbarAddButton.setOnClickListener {
            val action =
                ReminderListFragmentDirections.actionReminderListFragmentToReminderEditFragment()
            this.findNavController().navigate(action)
        }

        // bind segmented control to filter lists
        binding.segmentedControlGroup.setOnSelectedOptionChangeCallback { selectedIndex ->
            if (selectedIndex == 0) {
                viewModel.setReminderFilterMode(ReminderFilterMode.TODAY)
            } else if (selectedIndex == 1) {
                viewModel.setReminderFilterMode(ReminderFilterMode.FUTURE)
            } else {
                viewModel.setReminderFilterMode(ReminderFilterMode.ALL)
            }
        }

        // set up recycler view adapter
        val adapter = ReminderListAdapter(viewModel) {
            val action =
                ReminderListFragmentDirections.actionReminderListFragmentToReminderDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        // observe all reminders
        viewModel.allReminders.observe(this.viewLifecycleOwner) { reminders ->
            currentList = reminders
            updateList(adapter)
            updateProgressViewWithAnim()
        }

        // observe the current filter mode
        viewModel.reminderFilterMode.observe(this.viewLifecycleOwner) {
            oldProgress = binding.progressView.progress

            updateList(adapter)
            binding.segmentedControlGroup.setSelectedIndex(
                viewModel.getReminderFilterModePosition(),
                false
            )
            updateProgressViewWithoutAnim()
        }

        // call swipe handler to delete reminders
        swipeHandler()
    }

    // Swipe handler to delete items from the list
    private fun swipeHandler() {
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
                val reminderToDelete = filteredList!![position]
                viewModel.deleteReminder(reminderToDelete)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    // update the displayed list based on the filter mode
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
    }

    // animate progress view
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgressViewWithAnim() {
        binding.progressView.max = filteredList?.size ?: 100

        progress = filteredList?.filter {
            it.isComplete
        }?.size ?: 0

        binding.progressView.setProgress(progress, true)
    }

    // alt version of animating when switching between filter modes
    private fun updateProgressViewWithoutAnim() {
        binding.progressView.max = filteredList?.size ?: 100

        progress = filteredList?.filter {
            it.isComplete
        }?.size ?: 0

        binding.progressView.progress = progress
    }
}