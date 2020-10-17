package org.openhdp.hdt.ui.dashboard

sealed class DashboardViewState {
    object Loading : DashboardViewState()

    data class Results(val items: ArrayList<DashboardItem>) : DashboardViewState()
}