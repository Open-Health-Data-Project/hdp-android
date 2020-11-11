package org.openhdp.hdt.ui.tracking

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.dao.CategoryDAO
import org.openhdp.hdt.data.dao.TimestampDAO
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.ui.settings.StartOfDay
import org.openhdp.hdt.ui.settings.StartOfDayUseCase
import org.openhdp.hdt.ui.tracking.addCounter.AddStopwatchViewState
import timber.log.Timber
import java.lang.Math.abs
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class TrackingViewModel @ViewModelInject constructor(
    private val stopwatchRepository: StopwatchRepository,
    private val categoryDAO: CategoryDAO,
    private val timestampDAO: TimestampDAO,
    private val startOfDayUseCase: StartOfDayUseCase
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
                val startOfDay = startOfDayUseCase.getCurrentStartOfDay()
                stopwatches.mapNotNull { it.asTrackingItem(categories, startOfDay) }
            }.onFailure {
                Timber.e(it, "initialize() failure ")
                _viewState.value = TrackingViewState.Error(it)
            }.onSuccess { stopwatches ->
                if (stopwatches.isEmpty()) {
                    _viewState.value = TrackingViewState.NoStopwatches
                } else {
                    _viewState.value = TrackingViewState.Results(ArrayList(stopwatches))
                    countdown()
                }
            }
        }
    }

    private suspend fun Stopwatch.asTrackingItem(
        categories: List<Category>,
        startOfDay: StartOfDay
    ): TrackingItem? {
        try {
            val stopWatchId = this.id
            val category = categories.firstOrNull { this.categoryId == it.id } ?: return null

            val now = Date()
            val currentTimeInMillis = now.time

            val startOfDayDate = Date(now.time)
            startOfDayDate.hours = startOfDay.hours
            startOfDayDate.minutes = startOfDay.minutes


            var totalTimeInMillis = 0L
            var stopWatchRunning = false

            val timestamps = timestampDAO.getTimestampsFrom(stopWatchId)
            val millisAfterReset = StartOfDayTimeCalculator().calculate(startOfDay, timestamps, now)

            timestamps.forEachIndexed { index, timestamp ->
                val stopTime = timestamp.stopTime
                val startTime = timestamp.startTime
                val startDate = Date(startTime)

                if (stopTime != null) {
                    //timer was paused before
                    totalTimeInMillis += kotlin.math.abs(stopTime - startTime)
                } else {

//                    val timeoutInMillis = TimeUnit.HOURS.toMillis(16L)
//                    val lastActivityDurationTillNow = kotlin.math.abs(currentTime - it.startTime)
                    totalTimeInMillis += kotlin.math.abs(currentTimeInMillis - startTime)
                    if (timestamps.lastIndex == index) {
                        stopWatchRunning = true
                    } //make it still running
                }
            }

            return TrackingItem(
                id,
                name,
                category.name,
                millisAfterReset,
                buttonState = PlaybackButtonState(if (stopWatchRunning) TrackState.ACTIVE else TrackState.INACTIVE),
                startOfDay,
                category.color
            )
        } catch (throwable: Throwable) {
            Timber.e(throwable, "failed to map to tracking item")
            return null
        }
    }

    private suspend fun stopCountdown() {
        runCatching { job?.cancelAndJoin() }.onFailure {
            Timber.d("failed cancel job")
        }
    }

    private fun countdown() {
        runBlocking { stopCountdown() }
        val oneSec = TimeUnit.SECONDS.toMillis(1)
        job = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                delay(oneSec)
                tick(oneSec)
            }
        }
    }

    private fun tick(duration: Long) {
        onState<TrackingViewState.Results> { currentState ->
            val updatedItems = currentState.items.map {
                if (it.isRunning()) {
                    val now = Date()
                    val nowMinutes = now.hours * 60 + now.minutes
                    val targetMillis =
                        if (it.startOfDay.totalMinutes() == nowMinutes && now.seconds == 0) {
                            0
                        } else {
                            it.millisTracked + duration
                        }
                    it.copy(millisTracked = targetMillis)
                } else {
                    it
                }
            }
            currentState.copy(items = ArrayList(updatedItems))
        }
    }

    override fun onCleared() {
        GlobalScope.launch { stopCountdown() }
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
        Timber.d("toggle timer, current state is ${item.buttonState.trackState.name}")

        onState<TrackingViewState.Results> { state ->
            val currentItems = state.items
            val currentItemIndex =
                currentItems.indexOfFirst { it.stopwatchId == item.stopwatchId }
            if (currentItemIndex != -1) {
                currentItems[currentItemIndex] =
                    item.copy(buttonState = item.buttonState.copy(isEnabled = false))
                state.copy(items = currentItems)
            } else {
                state
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            val toggleTime = Date().time

            runCatching {
                val timestamp = timestampDAO.lastTimestampOf(item.stopwatchId)
                if (timestamp != null && timestamp.stopTime == null) {
                    timestampDAO.updateTimestamp(timestamp.id, toggleTime)
                    TrackState.INACTIVE
                } else {
                    timestampDAO.createTimestamp(
                        Timestamp(
                            item.stopwatchId,
                            toggleTime
                        )
                    )
                    TrackState.ACTIVE
                }
            }
                .onFailure {
                    Timber.e(it, "failed to update item due to issue $it")
                }.onSuccess { trackState ->

                    onState<TrackingViewState.Results> { state ->
                        val currentItems = state.items
                        val currentItemIndex =
                            currentItems.indexOfFirst { it.stopwatchId == item.stopwatchId }
                        if (currentItemIndex != -1) {
                            currentItems[currentItemIndex] =
                                item.copy(
                                    buttonState = item.buttonState.copy(
                                        isEnabled = true,
                                        trackState = trackState
                                    )
                                )
                            state.copy(items = currentItems)
                        } else {
                            state
                        }
                    }
                }
        }

        onState<TrackingViewState.Results> { state ->
            val currentItems = state.items
            val currentItemIndex = currentItems.indexOfFirst { it.stopwatchId == item.stopwatchId }
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
            val firstIndex = items.indexOfFirst { it.stopwatchId == firstItem.stopwatchId }
            val secondIndex = items.indexOfFirst { it.stopwatchId == otherItem.stopwatchId }
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
            stopCountdown()
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
