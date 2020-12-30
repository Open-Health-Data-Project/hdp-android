package org.openhdp.hdt.ui.history.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemHistoryHeaderBinding

class DayHeaderItem(
    val label: String,
    val isExpanded: Boolean,
    val onExpand: () -> Unit
) :
    BindableItem<ItemHistoryHeaderBinding>(),
    DefaultExpandableItem {

    override fun bind(viewBinding: ItemHistoryHeaderBinding, position: Int) {
        viewBinding.label.text = label

        val rotation = if (isExpanded) -180f else 0f
        viewBinding.expandButton.animate().rotation(rotation)
        viewBinding.headerContainer.setOnClickListener { onExpand() }
    }

    override fun getLayout() = R.layout.item_history_header

    override fun initializeViewBinding(view: View): ItemHistoryHeaderBinding {
        return ItemHistoryHeaderBinding.bind(view)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return other is DayHeaderItem && other.label == label
    }
}
