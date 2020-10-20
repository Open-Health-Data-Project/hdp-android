package org.openhdp.hdt.ui.categories.addCategory

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import org.openhdp.hdt.data.entities.Category

data class AddCategoryViewState(
    val name: String = "",
    val colors: List<SelectableColor>,
    val isLoading: Boolean = false,
    val addButtonEnabled: Boolean = false,
    val added: Boolean = false,
    val cancelled: Boolean = false
) {
    fun asCategory(mapper: ColorMapper): Category {
        return Category(
            0,
            name,
            0,
            mapper.transform(requireNotNull(colors.first { it.selected }.color))
        )
    }
}

interface ColorMapper {
    @ColorInt
    fun transform(@ColorRes resId: Int): Int
}