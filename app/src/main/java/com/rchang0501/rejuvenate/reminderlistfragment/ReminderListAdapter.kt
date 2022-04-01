package com.rchang0501.rejuvenate.reminderlistfragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rchang0501.rejuvenate.R
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderListItemBinding
import com.rchang0501.rejuvenate.viewmodels.RejuvenateViewModel

class ReminderListAdapter(
    private val viewModel: RejuvenateViewModel,
    private val onReminderClicked: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderListAdapter.ReminderViewHolder>(DiffCallback) {

    class ReminderViewHolder(
        private val viewModel: RejuvenateViewModel,
        var binding: ReminderListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.apply {
                reminderTitle.text = reminder.title
                reminderDueDate.text = viewModel.reminderDueDateWithTimeText(reminder)
                if (!reminder.isComplete) {
                    completedButton.setIconResource(R.drawable.ic_hollow_circle)
                } else {
                    completedButton.setIconResource(R.drawable.ic_circle)
                }
                completedButton.setOnClickListener {
                    viewModel.changeCompleted(reminder)
                    Log.d("apdater", "${reminder.title} completed: ${reminder.isComplete}")
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderListAdapter.ReminderViewHolder {
        return ReminderViewHolder(
            viewModel,
            ReminderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReminderListAdapter.ReminderViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onReminderClicked(current)
        }

        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Reminder>() {
            override fun areItemsTheSame(oldReminder: Reminder, newReminder: Reminder): Boolean {
                return oldReminder == newReminder
            }

            override fun areContentsTheSame(oldReminder: Reminder, newReminder: Reminder): Boolean {
                return oldReminder.title == newReminder.title
            }

        }

    }

}