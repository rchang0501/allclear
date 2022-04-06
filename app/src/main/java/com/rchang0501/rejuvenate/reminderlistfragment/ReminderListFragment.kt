package com.rchang0501.rejuvenate.reminderlistfragment

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import kotlin.math.roundToInt

class ReminderListFragment : Fragment() {

    val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

    private var filteredList: List<Reminder>? = null
    private var currentList: List<Reminder>? = null

    private var progress: Int = 0
    private var oldProgress: Int = 0

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

        currentList = viewModel.allReminders.value

        binding.apply {
            progressView.max = currentList?.size ?: 100
            progressView.progress = currentList?.filter {
                it.isComplete
            }?.size ?: 0
        }

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
            updateProgressViewWithAnim()
        }

        viewModel.reminderFilterMode.observe(this.viewLifecycleOwner) {
            oldProgress = binding.progressView.progress

            updateList(adapter)
            binding.segmentedControlGroup.setSelectedIndex(
                viewModel.getReminderFilterModePosition(),
                false
            )
            updateProgressViewWithoutAnim()
            //updateProgressView()
            //updateProgressViewWithAnim()
            //binding.recyclerView.smoothScrollToPosition(0)
        }

        swipeHandler()
    }

    /*
    override fun onDestroyView() {
        viewModel.allReminders.removeObservers(this.viewLifecycleOwner)
        viewModel.allReminders.removeObservers(this.viewLifecycleOwner)

        super.onDestroyView()
    }*/

    private fun swipeHandler() {
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
                val reminderToDelete = filteredList!![position]
                viewModel.deleteReminder(reminderToDelete)
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
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgressViewWithAnim() {
        binding.progressView.max = filteredList?.size ?: 100

        progress = filteredList?.filter {
            it.isComplete
        }?.size ?: 0

        binding.progressView.setProgress(progress, true)
    }

    private fun updateProgressViewWithoutAnim() {
        binding.progressView.max = filteredList?.size ?: 100

        progress = filteredList?.filter {
            it.isComplete
        }?.size ?: 0

        binding.progressView.progress = progress
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgressView() {
        var newMax = filteredList?.size ?: 100
        var newProgress = filteredList?.filter {
            it.isComplete
        }?.size ?: 0

        var newMaxFloat: Double = newMax.toDouble()
        var newProgressFloat: Double = newProgress.toDouble()

        var diffProgress = oldProgress * (newProgressFloat/newMaxFloat)
        var diffProgressInt: Int = diffProgress.roundToInt()
        binding.progressView.setProgress(diffProgressInt, true)

        Log.d("thing oldProgress", "$oldProgress")
        Log.d("thing newProgress", "$newProgress")
        Log.d("thing newMax", "$newMax")
        Log.d("thing diffProgress", "$diffProgress")
        Log.d("thing diffProgressInt", "$diffProgressInt")
        Log.d("thing space", "")

        // after animation change
        //binding.progressView.max = newMax
        //progress = newProgress

        //binding.progressView.progress = progress
    }
}