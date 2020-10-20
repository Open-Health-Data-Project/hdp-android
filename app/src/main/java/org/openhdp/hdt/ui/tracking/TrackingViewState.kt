package org.openhdp.hdt.ui.tracking

sealed class TrackingViewState {
    object Loading : TrackingViewState()

    object NoStopwatches : TrackingViewState()

    data class Error(val issue: Throwable) : TrackingViewState()

    data class Results(val items: ArrayList<TrackingItem>) : TrackingViewState()
}
