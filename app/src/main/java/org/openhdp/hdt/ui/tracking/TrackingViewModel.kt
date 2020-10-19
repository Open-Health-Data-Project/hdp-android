package org.openhdp.hdt.ui.tracking

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.openhdp.hdt.R
import org.openhdp.hdt.data.StopwatchRepository
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class TrackingViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
) : ViewModel(), OnItemClickListener {

    private val _viewState = MutableLiveData<TrackingViewState>()

    val viewState: LiveData<TrackingViewState>
        get() = _viewState

    private var job: Job? = null

    fun midnight() = Date().apply {
        seconds = 0
        minutes = 0
        hours = 0
    }

    fun initialize() {
        _viewState.value = TrackingViewState.Results(
            arrayListOf(
                TrackingItem(
                    "0",
                    "Netflix",
                    "",
                    midnight().time + TimeUnit.HOURS.toMillis(2),
                    TrackState.INACTIVE,
                    R.color.colorAccent
                ),
                TrackingItem(
                    "1",
                    "spacer",
                    "",
                    midnight().time + TimeUnit.HOURS.toMillis(1),
                    TrackState.ACTIVE,
                    R.color.colorYellow
                ),
                TrackingItem(
                    "2",
                    "angielski",
                    "",
                    midnight().time,
                    TrackState.ACTIVE,
                    R.color.colorOrange
                )
            )
        )

        countdown()
    }

    private fun countdown() {
        job?.cancel()
        val oneSec = TimeUnit.SECONDS.toMillis(1)
        job = viewModelScope.async(Dispatchers.Main) {
            while (true) {
                delay(oneSec)
                tick(oneSec)
            }
        }
    }

    private fun tick(duration: Long) {
        onState<TrackingViewState.Results> { currentState ->
            val items = currentState.items
            val updatedItems = arrayListOf<TrackingItem>()
            for (item in items) {
                if (item.isRunning()) {
                    updatedItems.add(item.copy(timestamp = item.timestamp + duration))
                } else {
                    updatedItems.add(item)
                }
            }
            currentState.copy(items = updatedItems)
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

    private inline fun <reified T : TrackingViewState> onState(action: (currentState: T) -> TrackingViewState) {
        _viewState.value?.let { state ->
            if (state is T) {
                _viewState.value = action.invoke(state)
            }
        }
    }

    override fun toggleTimer(item: TrackingItem) {
        onState<TrackingViewState.Results> { state ->
            val currentItems = state.items
            val currentItemIndex = currentItems.indexOfFirst { it.id == item.id }
            if (currentItemIndex != -1) {
                currentItems[currentItemIndex] = item.toggled()
                state.copy(items = currentItems)
            } else {
                state
            }
        }
    }

    override fun onSettingsClick(item: TrackingItem) {

    }

    fun reorder(firstItem: TrackingItem, otherItem: TrackingItem) {
        Timber.d("reorder $firstItem <-> $otherItem")
        onState<TrackingViewState.Results> { state ->
            val items = state.items
            val firstIndex = items.indexOfFirst { it.id == firstItem.id }
            val secondIndex = items.indexOfFirst { it.id == otherItem.id }
            if (firstIndex != -1 && secondIndex != -1) {
                items[firstIndex] = otherItem
                items[secondIndex] = firstItem
                state.copy(items = items)
            } else {
                state
            }
        }
        countdown()
    }

    fun onDragStarted() {
        job?.cancel()
    }

    fun onDragEnded() {
        // TODO: advance the timers by the time when ticker was off due to dragging
        countdown()
    }
}