package org.openhdp.hdt.ui.categories.addCategory

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openhdp.hdt.R
import org.openhdp.hdt.data.dao.CategoryDAO

class AddCategoryViewModel @ViewModelInject constructor(
    private val categoryDAO: CategoryDAO
) : ViewModel() {

    private val _viewState = MutableLiveData<AddCategoryViewState>().apply {
        value = AddCategoryViewState(
            isLoading = true, colors = listOf(
                SelectableColor(R.color.colorYellow),
                SelectableColor(R.color.colorOrange),
                SelectableColor(R.color.colorPink),
                SelectableColor(R.color.colorPurple),
                SelectableColor(R.color.colorBlue),
                SelectableColor(R.color.colorAccent),
            )
        )
    }
    val viewState: LiveData<AddCategoryViewState> = _viewState

    fun initialize() {

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
            _viewState.value = state.copy(
                name = name,
                addButtonEnabled = determineAddButtonEnabled(name, state.colors)
            )
        }
    }

    private fun determineAddButtonEnabled(name: String, colors: List<SelectableColor>): Boolean {
        return name.isNotEmpty() && colors.any { it.selected }
    }

    fun onPicked(item: SelectableColor) {
        _viewState.value?.let { state ->
            val colors = state.colors.map { it.copy(selected = item.color == it.color) }
            _viewState.value = state.copy(
                colors = colors,
                addButtonEnabled = determineAddButtonEnabled(state.name, colors)
            )
        }
    }
}