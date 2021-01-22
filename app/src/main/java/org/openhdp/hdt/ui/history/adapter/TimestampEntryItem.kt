package org.openhdp.hdt.ui.history.adapter

import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.ItemHistoryEntryBinding
import org.openhdp.hdt.ui.history.TimestampEntry

class TimestampEntryItem(
    val entry: TimestampEntry,
    val listener: (TimestampEntry) -> Unit
) :
    BindableItem<ItemHistoryEntryBinding>() {

    override fun bind(viewBinding: ItemHistoryEntryBinding, position: Int) {
        viewBinding.label.text = "${entry.stopwatchName}:${entry.label}"
        viewBinding.expandButton.setOnClickListener {
            listener.invoke(entry)
        }
    }

    override fun getLayout() = R.layout.item_history_entry

    override fun initializeViewBinding(view: View): ItemHistoryEntryBinding {
        return ItemHistoryEntryBinding.bind(view)
    }

    override fun isSameAs(other: Item<*>): Boolean {
        return other is TimestampEntryItem && other.entry == entry
    }
}
