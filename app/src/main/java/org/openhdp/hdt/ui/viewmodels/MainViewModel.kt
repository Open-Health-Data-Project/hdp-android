package org.openhdp.hdt.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import org.openhdp.hdt.data.StopwatchRepository


class MainViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
): ViewModel() {
}