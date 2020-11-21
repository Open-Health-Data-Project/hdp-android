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
import org.openhdp.hdt.data.entities.Category
import timber.log.Timber


class CategoriesListViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<CategoriesViewState>()
    val viewState: LiveData<CategoriesViewState> = _viewState

    fun initialize() {

        _viewState.value = CategoriesViewState.Loading
        requestCategoryList()
    }

    private fun requestCategoryList() {
        viewModelScope.launch {
            runCatching { stopwatchRepository.categories() }
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

    fun addOrUpdateCategory(category: Category) {
        _viewState.value = CategoriesViewState.Loading
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.createOrUpdateCategory(category)
            }.onSuccess { requestCategoryList() }
                .onFailure {
                    Timber.e(it, "failed to add/update category")
                    _viewState.value = CategoriesViewState.Error(it)
                }
        }
    }

    fun attemptRemove(it: CategoryItem) {
        if (it is CategoryItem.Manual) {
            val category = it.category
            val categoryId = category.id
            viewModelScope.launch {
                runCatching {
                    val stopwatchesAffected = stopwatchRepository.stopwatches().filter {
                        it.categoryId == categoryId
                    }
                    if (stopwatchesAffected.isEmpty()) {
                        stopwatchRepository.deleteCategory(category)
                        requestCategoryList()
                    } else {
                        // delete is dangerous, prompt to delete stopwatches affected first
                        _viewState.value = CategoriesViewState.FailedToDeleteCategory(category)
                    }
                }.onFailure { _viewState.value = CategoriesViewState.Error(it) }
            }
        }
    }
}