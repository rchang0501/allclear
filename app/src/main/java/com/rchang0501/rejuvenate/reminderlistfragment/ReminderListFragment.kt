package com.rchang0501.rejuvenate.reminderlistfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.RejuvenateApplication
import com.rchang0501.rejuvenate.databinding.ReminderListFragmentBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModelFactory

class ReminderListFragment : Fragment() {

    val viewModel: RejuvenateViewModel by activityViewModels {
        RejuvenateViewModelFactory(
            (activity?.application as RejuvenateApplication).database.reminderDao()
        )
    }

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

        binding.toolbarAddButton.setOnClickListener {
            val action = ReminderListFragmentDirections.actionReminderListFragmentToReminderEditFragment()
            this.findNavController().navigate(action)
        }

        val adapter = ReminderListAdapter(viewModel) {
            val action =
                ReminderListFragmentDirections.actionReminderListFragmentToReminderDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        viewModel.allReminders.observe(this.viewLifecycleOwner) { reminders ->
            reminders.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
    }
}