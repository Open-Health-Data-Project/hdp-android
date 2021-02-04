package org.openhdp.hdt.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.ui.base.BaseViewModel
import org.openhdp.hdt.ui.settings.export.ExportCategoriesUseCase
import org.openhdp.hdt.ui.settings.export.ExportStopwatchesUseCase
import org.openhdp.hdt.ui.settings.export.ExportTimestampsUseCase
import timber.log.Timber

class SettingsViewModel @ViewModelInject constructor(
    private val stopwatchRepository: StopwatchRepository,
    private val exportStopwatchesUseCase: ExportStopwatchesUseCase,
    private val exportCategoriesUseCase: ExportCategoriesUseCase,
    private val exportTimestampsUseCase: ExportTimestampsUseCase,
    private val startOfDayUseCase: StartOfDayUseCase
) : BaseViewModel<SettingsViewState>() {

    fun initialize() {
        val startOfDay = startOfDayUseCase.getCurrentStartOfDay()
        setState(
            SettingsViewState.Display(startOfDay.hours, startOfDay.minutes)
        )
    }

    fun onEvent(event: SettingsEvent) = when (event) {
        SettingsEvent.ExportStopwatches -> {
            exportStopwatchesData()
        }
        is SettingsEvent.ExportTimestamps -> {
            exportStopwatchTimestamps(event.stopwatch)
        }
        is SettingsEvent.ChangeStartOfDay -> {
            startOfDayUseCase.saveStartOfDay(event.hourOfDay, event.minute)
            initialize()
        }
        SettingsEvent.RequestGenericExportDialog -> {
            requestExportDialog()
        }
        SettingsEvent.ExportCategories -> {
            requestExportCategories()
        }
        SettingsEvent.ExportAllTimestamps -> {
            exportAllStopwatchTimestamps()
        }
    }

    private fun exportAllStopwatchTimestamps() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val timestamps = stopwatchRepository.allTimestamps()
                Timber.e("timestamps(${timestamps.size})")
                if (timestamps.isNotEmpty()) {
                    setState(
                        SettingsViewState.Share(
                            exportTimestampsUseCase.exportAll(timestamps)
                        )
                    )
                } else {
                    setState(SettingsViewState.Error(NothingToExportException()))
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun exportStopwatchTimestamps(stopwatch: Stopwatch) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val timestamps = stopwatchRepository.timestamps(stopwatch.id)
                Timber.e("timestamps( ${timestamps.size}")
                if (timestamps.isNotEmpty()) {
                    setState(
                        SettingsViewState.Share(
                            exportTimestampsUseCase.export(
                                stopwatch.id,
                                timestamps
                            )
                        )
                    )
                } else {
                    setState(SettingsViewState.Error(NothingToExportException()))
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun requestExportCategories() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val categories = stopwatchRepository.categories()
                Timber.e("requestExportCategories ${categories.size}")
                if (categories.isNotEmpty()) {
                    setState(SettingsViewState.Share(exportCategoriesUseCase.export(categories)))
                } else {
                    setState(SettingsViewState.Error(NothingToExportException()))
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun requestExportDialog() {
        viewModelScope.launch(Dispatchers.Main) {

            val stopwatches = stopwatchRepository.stopwatches()
            setState(SettingsViewState.DisplayExportPicker(stopwatches))
        }
    }

    private fun exportStopwatchesData() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.stopwatches()
            }.onSuccess { stopwatches ->
                if (stopwatches.isNotEmpty()) {
                    setState(
                        SettingsViewState.Share(
                            exportStopwatchesUseCase.export(stopwatches)
                        )
                    )
                } else {
                    setState(SettingsViewState.Error(NothingToExportException()))
                }

            }.onFailure { issue ->
                setState(SettingsViewState.Error(issue))
            }
        }
    }

    fun requestStopwatches(callback: (List<Stopwatch>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.stopwatches()
            }.onSuccess { stopwatches -> callback(stopwatches) }
        }
    }

    fun requestStopwatchData(stopwatch: Stopwatch, function: (Category, Timestamp) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            stopwatchRepository.categories().firstOrNull {
                it.id == stopwatch.categoryId
            }?.let { category ->
                stopwatchRepository.lastTimestampOf(stopwatch.id)?.let {
                    function(category, it)
                }
            }
        }
    }
}

class NothingToExportException : Exception()
