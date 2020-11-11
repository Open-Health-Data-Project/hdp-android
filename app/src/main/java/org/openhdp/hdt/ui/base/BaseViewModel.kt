package org.openhdp.hdt.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<ViewState : Any>(initialViewState: ViewState? = null) : ViewModel() {

    protected val _viewState: MutableLiveData<ViewState> = if (initialViewState == null) {
        MutableLiveData<ViewState>()
    } else {
        MutableLiveData<ViewState>(initialViewState)
    }

    val viewState: LiveData<ViewState>
        get() = _viewState

    protected inline fun <reified State : ViewState> pushState(reducer: (previousState: State) -> State) {
        val currentState = viewState.value
        if (currentState is State) {
            _viewState.value = reducer.invoke(currentState)
        }
    }

    protected fun setState(nextState: ViewState) {
        _viewState.value = nextState
    }

    protected inline fun <reified T : ViewState> whenState(reducer: (T) -> Unit) {
        val currentState = _viewState.value as? T ?: return
        reducer.invoke(currentState)
    }
}