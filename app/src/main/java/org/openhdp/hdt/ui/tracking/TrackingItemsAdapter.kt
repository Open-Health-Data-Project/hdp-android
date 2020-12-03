package org.openhdp.hdt.ui.tracking

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemTimerBinding
import java.util.concurrent.TimeUnit

interface OnItemClickListener {
    fun toggleTimer(item: TrackingItem)
    fun onTimerTapped(item: TrackingItem)
    fun onHistoryButtonClick(item: TrackingItem)
}

interface OnDragChangeListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    fun onEndDrag()
}

class DashboardItemsAdapter : RecyclerView.Adapter<DashboardItemViewHolder>() {

    var dragChangeListener: OnDragChangeListener? = null
    var listener: OnItemClickListener? = null
    var items: List<TrackingItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        binding.categoryName.text = item.categoryName

        formatElapsedTime(item.millisTracked)
    }

    fun formatElapsedTime(millis: Long) {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        val minutes = totalSeconds / 60
        val hours = totalSeconds / 3600
        binding.timerTime.text =
            String.format("%02d:%02d:%02d", hours, minutes.rem(60), totalSeconds.rem(60))
    }

    private fun makeRoundedCorners(@ColorInt color: Int) {
        val radius = itemView.resources.getDimension(R.dimen.cardview_corner_size)
        binding.root.setupCorners(R.color.white) {
            setAllCorners(CornerFamily.ROUNDED, radius)
        }
        binding.categoryName.setupCornersWithColor(color) {
            setTopLeftCorner(CornerFamily.ROUNDED, radius)
            setTopRightCorner(CornerFamily.ROUNDED, radius)
        }
    }

    private fun bindButtons(item: TrackingItem, listener: OnItemClickListener?) {
        val icon = if (item.buttonState.trackState == TrackState.ACTIVE) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        val tintList = if (item.buttonState.isEnabled) {
            ColorStateList.valueOf(item.color)
        } else {
            ColorStateList.valueOf(Color.DKGRAY)
        }
        binding.playOrPause.backgroundTintList = tintList
        binding.playOrPause.isEnabled = item.buttonState.isEnabled
        binding.playOrPause.setImageResource(icon)

        binding.playOrPause.setOnClickListener {
            listener?.toggleTimer(item)
        }
        binding.history.isEnabled = item.buttonState.isEnabled
        binding.history.backgroundTintList = tintList
        binding.history.setOnClickListener {
            listener?.onHistoryButtonClick(item)
        }
        val onItemClick: (View) -> Unit = {
            listener?.onTimerTapped(item)
        }
        binding.root.setOnClickListener(onItemClick)
        binding.categoryName.setOnClickListener(onItemClick)
        binding.timerTime.setOnClickListener(onItemClick)
        binding.timerName.setOnClickListener(onItemClick)
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
    val color = ContextCompat.getColor(context, colorRes)
    setupCornersWithColor(color, block)
}

fun View.setupCornersWithColor(
    @ColorInt color: Int,
    block: ShapeAppearanceModel.Builder.() -> Unit
) {
    val shapeAppearanceModel = ShapeAppearanceModel()
        .toBuilder().apply(block)
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    shapeDrawable.fillColor = ColorStateList.valueOf(color)
    ViewCompat.setBackground(this, shapeDrawable)
}
