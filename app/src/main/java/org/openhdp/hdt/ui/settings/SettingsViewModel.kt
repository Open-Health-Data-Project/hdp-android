package org.openhdp.hdt.ui.settings

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.base.BaseViewModel
import org.openhdp.hdt.ui.history.ExportToCsvInteractor
import java.lang.IllegalStateException

class SettingsViewModel @ViewModelInject constructor(
    private val stopwatchRepository: StopwatchRepository,
    private val exportStopwatchesUseCase: ExportStopwatchesUseCase,
    private val exportTimestampsUseCase: ExportTimestampsUseCase
) : BaseViewModel<SettingsViewState>() {

    fun onEvent(event: SettingsEvent) = when (event) {
        SettingsEvent.RequestExportStopwatches -> {
            fetchStopwatchesData()
        }
        SettingsEvent.RequestStopwatchPicker -> {
            requestStopwatchPicker()
        }
        is SettingsEvent.RequestExportStopwatchTimestamps -> {
            fetchTimestampsData(event.stopwatch)
        }
        is SettingsEvent.ExportStopwatches -> {
            exportStopwatches(event.uri)
        }
        is SettingsEvent.ExportStopwatchTimestamps -> {
            exportStopwatchTimestamps(event.uri)
        }
    }

    private fun fetchStopwatchesData() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.stopwatches()
            }.onSuccess { stopwatches ->
                setState(SettingsViewState.ExportStopwatchesData(stopwatches))
            }.onFailure { issue ->
                setState(SettingsViewState.Error(issue))
            }
        }
    }

    private fun requestStopwatchPicker() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.stopwatches()
            }.onSuccess { stopwatches ->
                setState(SettingsViewState.DisplayStopwatchPicker(stopwatches))
            }.onFailure { issue ->
                setState(SettingsViewState.Error(issue))
            }
        }
    }

    private fun fetchTimestampsData(stopwatch: Stopwatch) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.timestamps(stopwatch.id)
            }.onSuccess {
                if (it.isEmpty()) {
                    setState(SettingsViewState.Error(IllegalStateException("timestamps should not be empty")))
                } else {
                    setState(SettingsViewState.ExportStopwatchTimestamps(stopwatch, it))
                }
            }.onFailure {
                setState(SettingsViewState.Error(it))
            }
        }
    }

    private fun exportStopwatches(uri: Uri) = whenState<SettingsViewState.ExportStopwatchesData> {
        exportStopwatchesUseCase.export(uri, it.stopwatches)
    }

    private fun exportStopwatchTimestamps(uri: Uri) {
        whenState<SettingsViewState.ExportStopwatchTimestamps> {
            exportTimestampsUseCase.export(uri, it.stopwatch.id, it.timestamps)
        }

    }
}
