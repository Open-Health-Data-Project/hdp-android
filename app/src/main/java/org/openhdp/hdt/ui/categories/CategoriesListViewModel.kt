package org.openhdp.hdt.ui.categories

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.dao.CategoryDAO
import org.openhdp.hdt.data.dao.IndependentCategoriesDAO
import org.openhdp.hdt.data.entities.Category
import timber.log.Timber


class CategoriesListViewModel @ViewModelInject constructor(
    val categoriesDAO: CategoryDAO
) : ViewModel() {

    private val _viewState = MutableLiveData<CategoriesViewState>()
    val viewState: LiveData<CategoriesViewState> = _viewState

    fun initialize() {

        _viewState.value = CategoriesViewState.Loading
        requestCategoryList()
    }

    private fun requestCategoryList() {
        viewModelScope.launch {
            runCatching { categoriesDAO.getAllCategoriesOrdered() }
                .onSuccess { categories ->
                    if (categories.isEmpty()) {
                        _viewState.value = CategoriesViewState.NoCategories
                    } else {
                        _viewState.value =
                            CategoriesViewState.Results(categories.map {
                                CategoryItem.Manual(it)
                            })
                    }
                }.onFailure {
                    _viewState.value = CategoriesViewState.Error(it)
                }
        }
    }

    fun addNewCategory(category: Category) {
        Timber.d("add new category $category")
        _viewState.value = CategoriesViewState.Loading
        viewModelScope.launch(Dispatchers.Main) {
            runCatching { categoriesDAO.createCategory(category) }
                .onSuccess { requestCategoryList() }
                .onFailure {
                    Timber.e(it, "failed to add category")
                    _viewState.value = CategoriesViewState.Error(it)
                }
        }
    }

    fun deleteCategory(categoryItem: CategoryItem) {
        if (categoryItem is CategoryItem.Manual) {

            _viewState.value = CategoriesViewState.Loading
            viewModelScope.launch {
                val cat = categoryItem.category
                runCatching { categoriesDAO.deleteCategory(cat) }
                    .onSuccess { requestCategoryList() }
                    .onFailure {
                        Timber.e(it, "failed to remove category")

                        _viewState.value = CategoriesViewState.Error(it)
                    }
            }
        } else {
            Timber.w("delete not supported for $categoryItem")
        }
    }

    fun updateCategory(category: Category) {
        _viewState.value = CategoriesViewState.Loading
        viewModelScope.launch {
            runCatching { categoriesDAO.deleteCategory(category) }
                .onSuccess { requestCategoryList() }
                .onFailure { _viewState.value = CategoriesViewState.Error(it) }
        }
    }
}