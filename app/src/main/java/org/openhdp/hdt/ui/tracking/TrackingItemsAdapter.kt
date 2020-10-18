package org.openhdp.hdt.ui.tracking

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemTimerBinding
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss", Locale.ROOT)

interface OnItemClickListener {
    fun toggleTimer(item: TrackingItem)
    fun onSettingsClick(item: TrackingItem)
}

interface OnDragChangeListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    fun onEndDrag()
}

class DashboardItemsAdapter : RecyclerView.Adapter<DashboardItemViewHolder>() {

    var dragChangeListener: OnDragChangeListener? = null
    var listener: OnItemClickListener? = null
    var items = listOf<TrackingItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        holder.bind(items[position], listener, dragChangeListener)
    }

    override fun getItemCount() = items.size
}

class DashboardItemViewHolder(
    private val binding: ItemTimerBinding
) : RecyclerView.ViewHolder(binding.root) {

    var item: TrackingItem? = null

    @SuppressLint("ClickableViewAccessibility")
    fun bind(
        item: TrackingItem,
        listener: OnItemClickListener?,
        dragChangeListener: OnDragChangeListener?
    ) {
        this.item = item
        makeRoundedCorners(item.color)
        bindButtons(item, listener)
        binding.root.setOnTouchListener(wrapDragChanges(dragChangeListener))
        binding.timerName.text = item.name
        val date = Date().apply { time = item.timestamp }
        binding.timerTime.text = DATE_FORMAT.format(date.time)
    }

    private fun makeRoundedCorners(@ColorRes color: Int) {
        val radius = itemView.resources.getDimension(R.dimen.cardview_corner_size)
        binding.root.setupCorners(R.color.white) {
            setAllCorners(CornerFamily.ROUNDED, radius)
        }
        binding.timerName.setupCorners(color) {
            setTopLeftCorner(CornerFamily.ROUNDED, radius)
            setTopRightCorner(CornerFamily.ROUNDED, radius)
        }
    }

    private fun bindButtons(item: TrackingItem, listener: OnItemClickListener?) {
        val icon = if (item.state == TrackState.ACTIVE) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        binding.playOrPause.setImageResource(icon)
        val context = itemView.context
        val tintList = ColorStateList.valueOf(ContextCompat.getColor(context, item.color))
        binding.playOrPause.backgroundTintList = tintList
        binding.playOrPause.setOnClickListener {
            listener?.toggleTimer(item)
        }
        binding.history.backgroundTintList = tintList
        binding.history.setOnClickListener {
            listener?.onSettingsClick(item)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun wrapDragChanges(dragChangeListener: OnDragChangeListener?): View.OnTouchListener {
        return View.OnTouchListener { _, _event ->
            val event = _event ?: return@OnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
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

fun RecyclerView.ViewHolder.asDashboardItem(): TrackingItem? {
    return (this as? DashboardItemViewHolder)?.item
}

fun View.setupCorners(
    @ColorRes colorRes: Int,
    block: ShapeAppearanceModel.Builder.() -> Unit
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder().apply(block)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    shapeDrawable.fillColor = ContextCompat.getColorStateList(context, colorRes)
    ViewCompat.setBackground(this, shapeDrawable)
}
