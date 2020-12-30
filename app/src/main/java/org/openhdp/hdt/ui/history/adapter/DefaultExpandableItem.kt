package org.openhdp.hdt.ui.history.adapter

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem

interface DefaultExpandableItem : ExpandableItem {
    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
    }
}
