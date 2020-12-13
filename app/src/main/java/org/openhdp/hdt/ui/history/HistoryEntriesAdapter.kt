package org.openhdp.hdt.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.databinding.ItemTimeEntryBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


val DATE_FORMAT = SimpleDateFormat("HH:mm:ss (dd-MMM-yyyy)", Locale.ENGLISH)

val diff = object : DiffUtil.ItemCallback<Timestamp>() {
    override fun areItemsTheSame(oldItem: Timestamp, newItem: Timestamp): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Timestamp, newItem: Timestamp): Boolean {
        return oldItem.id == newItem.id
    }
}

class HistoryEntriesAdapter :
    androidx.recyclerview.widget.ListAdapter<Timestamp, HistoryViewHolder>(diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            ItemTimeEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.entryName.text = item.asDuration()
    }

    private fun Timestamp?.asDuration(): String {
        if (this == null) {
            return ""
        } else {
            val stopTime = stopTime
            return if (stopTime == null) {
                "Started ${DATE_FORMAT.format(Date(startTime))} and still runs"
            } else {
                val diff = kotlin.math.abs(stopTime - startTime)

                val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(diff)
                val minutes = totalSeconds / 60
                val hours = totalSeconds / 3600
                val duration =
                    String.format("%02d:%02d:%02d", hours, minutes.rem(60), totalSeconds.rem(60))

                "Started ${DATE_FORMAT.format(Date(startTime))} " +
                        "\nEnded ${DATE_FORMAT.format(Date(stopTime))}" +
                        "\n($duration total)"
            }
        }
    }
}

class HistoryViewHolder(val binding: ItemTimeEntryBinding) : RecyclerView.ViewHolder(binding.root)