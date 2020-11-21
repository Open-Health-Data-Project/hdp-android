package org.openhdp.hdt.ui.categories.addCategory

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddCategoryViewModel @ViewModelInject constructor(
    private val colorsRepository: SelectableColorsRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<AddCategoryViewState>().apply {
        value = AddCategoryViewState(
            isLoading = true,
            colors = colorsRepository.provideColors()
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