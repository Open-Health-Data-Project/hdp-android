package org.openhdp.hdt.ui.categories

import org.openhdp.hdt.data.entities.Category

sealed class CategoriesViewState {

    object Loading : CategoriesViewState()

    object NoCategories : CategoriesViewState()

    data class Results(val categories: List<CategoryItem>) : CategoriesViewState()

    data class Error(val issue: Throwable) : CategoriesViewState()

    data class FailedToDeleteCategory(val category: Category) : CategoriesViewState()
}

sealed class CategoryItem {

    data class Independent(val name: String, val id: Int) : CategoryItem()

    data class Manual(val category: Category) : CategoryItem()
}
