package org.openhdp.hdt.ui.tracking.addCounter

import org.openhdp.hdt.data.entities.Category

data class SelectableCategory(val category: Category, val selected: Boolean = false) {
    val id = category.id
}