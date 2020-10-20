package org.openhdp.hdt.ui.tracking.addCounter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.dao.CategoryDAO

class AddStopwatchViewModel @ViewModelInject constructor(
    private val categoryDAO: CategoryDAO
) : ViewModel() {

    private val _viewState = MutableLiveData<AddStopwatchViewState>().apply {
        value = AddStopwatchViewState(isLoading = true)
    }
    val viewState: LiveData<AddStopwatchViewState> = _viewState

    fun initialize() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching { categoryDAO.getAllCategoriesOrdered() }
                .onFailure {
                    _viewState.value = AddStopwatchViewState(
                        isLoading = false,
                        cancelled = true,
                        categories = emptyList(),
                    )
                }
                .onSuccess { categories ->
                    val cancelled = categories.isEmpty()
                    _viewState.value = AddStopwatchViewState(
                        isLoading = false,
                        cancelled = cancelled,
                        categories = categories.map {
                            SelectableCategory(it)
                        }
                    )
                }
        }
    }

    fun onCancel() {
        _viewState.value?.let { state ->
            _viewState.value = state.copy(cancelled = true)
        }
    }

    fun onAdded() {
        _viewState.value?.let { state ->
            if (state.addButtonEnabled) {
                _viewState.value = state.copy(added = true)
            }
        }
    }

    fun onNameChanged(name: String) {
        _viewState.value?.let { state ->
            _viewState.value =
                state.copy(name = name, addButtonEnabled = determineAddButtonEnabled(state))
        }
    }

    fun onCategoryPicked(selectedOne: SelectableCategory) {
        _viewState.value?.let { state ->
            val newCategories = state.categories.map { category ->
                category.copy(selected = category.id == selectedOne.id)
            }
            _viewState.value = state.copy(
                categories = newCategories,
                addButtonEnabled = determineAddButtonEnabled(state)
            )
        }
    }

    private fun determineAddButtonEnabled(state: AddStopwatchViewState): Boolean {
        return state.name.isNotEmpty() && state.categories.any { it.selected }
    }
}
