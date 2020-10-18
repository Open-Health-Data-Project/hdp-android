package org.openhdp.hdt.ui.tracking

sealed class TrackingViewState {
    object Loading : TrackingViewState()

    data class Results(val items: ArrayList<TrackingItem>) : TrackingViewState()
}
