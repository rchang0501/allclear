package com.rchang0501.rejuvenate.reminderlistfragment

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

    // represents the view of the row item, binds data to the ui
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
                }
            }
        }
    }

    // creates and inflates the reminder list item layout
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderListAdapter.ReminderViewHolder {
        return ReminderViewHolder(
            viewModel,
            ReminderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // binds data to ui and sets navigation function upon click of recycler view holder
    override fun onBindViewHolder(holder: ReminderListAdapter.ReminderViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onReminderClicked(current)
        }

        holder.bind(current)
    }

    // diff call back to determine what values changed
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