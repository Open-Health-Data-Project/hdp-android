package org.openhdp.hdt.ui.dashboard

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemTimerBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss", Locale.ROOT)

interface OnDashboardItemClickListener {
    fun toggleTimer(item: DashboardItem)
    fun onSettingsClick(item: DashboardItem)
}

interface OnDragChangeListener {
    fun onStartDrag(viewHolder: DashboardItemViewHolder)
    fun onEndDrag()
}

class DashboardItemsAdapter : RecyclerView.Adapter<DashboardItemViewHolder>() {

    var dragChangeListener: OnDragChangeListener? = null
    var dashboardListener: OnDashboardItemClickListener? = null

    var items = listOf<DashboardItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        holder.bind(items[position], dashboardListener, dragChangeListener)
    }

    override fun getItemCount() = items.count()
}


class DashboardItemViewHolder(
    private val binding: ItemTimerBinding
) : RecyclerView.ViewHolder(binding.root) {

    var item: DashboardItem? = null

    @SuppressLint("ClickableViewAccessibility")
    fun bind(
        item: DashboardItem,
        listener: OnDashboardItemClickListener?,
        dragChangeListener: OnDragChangeListener?
    ) {
        this.item = item
        binding.root.setOnTouchListener(wrapDragChanges(dragChangeListener))
        val icon = if (item.state == DashboardItemState.ACTIVE) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        binding.playOrPause.setImageResource(icon)

        binding.timerName.text = item.name

        val date = Date().apply { time = item.timestamp }
        binding.timerTime.text = DATE_FORMAT.format(date.time)

        binding.playOrPause.setOnClickListener {
            listener?.toggleTimer(item)
        }
        binding.settings.setOnClickListener {
            listener?.onSettingsClick(item)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun wrapDragChanges(dragChangeListener: OnDragChangeListener?): View.OnTouchListener {
        return View.OnTouchListener { _, _event ->
            val event = _event ?: return@OnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Timber.w("down")
                    dragChangeListener?.onStartDrag(this@DashboardItemViewHolder)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    dragChangeListener?.onEndDrag()
                    true
                }
                else -> {
                    false
                }
            }

        }
    }
}


fun RecyclerView.ViewHolder.asDashboardItem(): DashboardItem? {
    val item = (this as? DashboardItemViewHolder)?.item
    Timber.d("as dashboardItem ${item}")
    return item
}