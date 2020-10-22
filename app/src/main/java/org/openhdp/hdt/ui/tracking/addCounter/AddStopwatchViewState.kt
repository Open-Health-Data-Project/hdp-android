package org.openhdp.hdt.ui.tracking.addCounter


data class AddStopwatchViewState(
    val name: String = "",
    val isLoading: Boolean = false,
    val categories: List<SelectableCategory> = emptyList(),
    val addButtonEnabled: Boolean = false,
    val added: Boolean = false,
    val cancelled: Boolean = false
)