package org.openhdp.hdt.ui.tracking

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.openhdp.hdt.R
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.dao.CategoryDAO
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.tracking.addCounter.AddStopwatchViewState
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class TrackingViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository,
    val categoryDAO: CategoryDAO
) : ViewModel(), OnItemClickListener {

    private val _viewState = MutableLiveData<TrackingViewState>()

    val viewState: LiveData<TrackingViewState>
        get() = _viewState

    private var job: Job? = null

    fun initialize() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val stopwatches = stopwatchRepository.stopwatchDAO.getAllStopwatchesInOrder()
                val categories = categoryDAO.getAllCategoriesOrdered()
                stopwatches.mapNotNull { it.asTrackingItem(categories) }
            }.onFailure {
                Timber.e(it, "initialize() failure ")
                _viewState.value = TrackingViewState.Error(it)
            }.onSuccess { stopwatches ->
                if (stopwatches.isEmpty()) {
                    _viewState.value = TrackingViewState.NoStopwatches
                } else {
                    _viewState.value = TrackingViewState.Results(ArrayList(stopwatches))

                }
            }
        }
        _viewState.value = TrackingViewState.NoStopwatches
    }

    private fun Stopwatch.asTrackingItem(categories: List<Category>): TrackingItem? {
        try {
            val category = categories.firstOrNull { this.categoryId == it.id } ?: return null
            return TrackingItem(
                id.toString(),
                name,
                category.name,
                Date().time,
                TrackState.INACTIVE,
                category.color
            )
        } catch (throwable: Throwable) {
            Timber.e(throwable, "failed to map to tracking item")
            return null
        }
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
    }

    fun onDragStarted() {
        job?.cancel()
    }

    fun onDragEnded() {

    }

    fun onCounterAdded(item: AddStopwatchViewState) {
        val selectedCategoryId = item.categories.firstOrNull { it.selected }?.category?.id ?: return

        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val count = stopwatchRepository.stopwatchDAO.getAllStopwatchesCount()
                val stopwatch = Stopwatch(count + 1, item.name, selectedCategoryId)
                stopwatchRepository.stopwatchDAO.createStopwatch(stopwatch)
            }.onFailure {
                Timber.e(it, "onCounterAdded($item) failure ")
                _viewState.postValue(TrackingViewState.Error(it))
            }.onSuccess {
                initialize()
            }
        }
    }
}
