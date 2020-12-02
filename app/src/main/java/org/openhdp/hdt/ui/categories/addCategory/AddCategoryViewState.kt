package org.openhdp.hdt.ui.categories.addCategory

import org.openhdp.hdt.data.entities.Category

data class AddCategoryViewState(
    val name: String = "",
    val colors: List<SelectableColor>,
    val isLoading: Boolean = false,
    val addButtonEnabled: Boolean = false,
    val added: Boolean = false,
    val cancelled: Boolean = false
) {
    fun asCategory(): Category {
        return Category(
            0,
            name,
            0,
            currentColor()
        )
    }

    fun currentColor(): Int {
        return colors.first { it.selected }.color
    }
}