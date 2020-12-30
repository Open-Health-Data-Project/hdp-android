package org.openhdp.hdt.ui.history.adapter

import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemHistoryEntryBinding
import org.openhdp.hdt.databinding.ItemHistoryHeaderBinding

class TimestampEntryItem(
    val text: String,
    val stopwatchName: String
) :
    BindableItem<ItemHistoryEntryBinding>() {

    override fun bind(viewBinding: ItemHistoryEntryBinding, position: Int) {
        viewBinding.label.text = "$stopwatchName: $text"
    }

    override fun getLayout() = R.layout.item_history_entry

    override fun initializeViewBinding(view: View): ItemHistoryEntryBinding {
        return ItemHistoryEntryBinding.bind(view)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return other is TimestampEntryItem && other.text == text
    }
}
