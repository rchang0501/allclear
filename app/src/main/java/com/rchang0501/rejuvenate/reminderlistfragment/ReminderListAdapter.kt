package com.rchang0501.rejuvenate.reminderlistfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rchang0501.rejuvenate.data.Reminder
import com.rchang0501.rejuvenate.databinding.ReminderListReminderBinding

class ReminderListAdapter(private val onReminderClicked: (Reminder) -> Unit): ListAdapter<Reminder, ReminderListAdapter.ReminderViewHolder>(DiffCallback) {

    class ReminderViewHolder(private var binding: ReminderListReminderBinding): RecyclerView.ViewHolder(binding.root){
        fun bind (reminder: Reminder){
            binding.apply{
                reminderTitle.text = reminder.title
                reminderDueDate.text = reminder.dueDate
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderListAdapter.ReminderViewHolder {
        return ReminderViewHolder(
            ReminderListReminderBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ReminderListAdapter.ReminderViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener{
            onReminderClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Reminder>(){
            override fun areItemsTheSame(oldReminder: Reminder, newReminder: Reminder): Boolean {
                return oldReminder == newReminder
            }

            override fun areContentsTheSame(oldReminder: Reminder, newReminder: Reminder): Boolean {
                return oldReminder.title == newReminder.title
            }

        }

    }

}