package org.openhdp.hdt.ui.tracking.stopwatchDetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.ui.base.BaseViewModel
import org.openhdp.hdt.ui.history.HistoryViewState
import org.openhdp.hdt.ui.tracking.TrackingItem
import timber.log.Timber


class StopwatchDetailViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
) : BaseViewModel<StopwatchDetailViewState>(
    StopwatchDetailViewState.Loading
) {

    lateinit var stopwatch: TrackingItem

    fun initialize(stopwatch: TrackingItem) {
        this.stopwatch = stopwatch
        setState(StopwatchDetailViewState.Display(isInEditMode = false, stopwatch.name))
    }

    fun onStopwatchNameChanged(name: String) {
        pushState<StopwatchDetailViewState.Display> {
            it.copy(stopwatchName = name)
        }
    }

    fun toggleEditMode() {
        pushState<StopwatchDetailViewState.Display> {
            if (it.stopwatchName.isNotEmpty()) {
                if (it.isInEditMode) {
                    updateStopwatchIfPossible(it.stopwatchName)
                }
                it.copy(isInEditMode = !it.isInEditMode)
            } else {
                it.copy(errorMessage = "Name cannot be empty")
            }
        }
    }

    private fun updateStopwatchIfPossible(name: String) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching { stopwatchRepository.updateStopwatchName(stopwatch.stopwatchId, name) }
                .onFailure { Timber.e(it) }
        }
    }

    fun delete() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching { stopwatchRepository.deleteStopwatch(stopwatch.stopwatchId) }
                .onFailure { Timber.e(it) }
        }
    }
}

sealed class StopwatchDetailViewState {

    data class Display(
        val isInEditMode: Boolean,
        val stopwatchName: String,
        val errorMessage: String? = null
    ) : StopwatchDetailViewState()

    object Loading : StopwatchDetailViewState()
}
