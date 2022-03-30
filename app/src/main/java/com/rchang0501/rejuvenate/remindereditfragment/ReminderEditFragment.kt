package com.rchang0501.rejuvenate.remindereditfragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.databinding.ReminderEditFragmentBinding

class ReminderEditFragment : Fragment() {

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTimePickerDialog(v: View) {
        ///val timePicker = TimePickerDialog(this, R.style.TimerPickerDialog)
        TimePickerFragment().show(childFragmentManager, "timePicker")
    }
}