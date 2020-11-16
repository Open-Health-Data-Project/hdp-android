package org.openhdp.hdt.ui.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp


@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()


    lateinit var viewModel: HistoryViewModel

    val viewStateObserver = mock<Observer<HistoryViewState>>()

    val stopwatchRepository = mock<StopwatchRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HistoryViewModel(stopwatchRepository)
        viewModel.viewState.observeForever(viewStateObserver)
    }

    @After
    fun after() {
        viewModel.viewState.removeObserver(viewStateObserver)
        Dispatchers.resetMain()
    }

    @Test
    fun `renders results`() = runBlockingTest {
        val stopwatches = listOf(mock<Stopwatch>(), mock<Stopwatch>())
        whenever(stopwatchRepository.stopwatches()).doReturn(stopwatches)

        viewModel.initialize()

        verify(viewStateObserver).onChanged(HistoryViewState.Stopwatches(stopwatches))
    }

    @Test
    fun `renders no stopwatches`() = runBlockingTest {
        whenever(stopwatchRepository.stopwatches()).doReturn(emptyList())

        viewModel.initialize()

        verify(viewStateObserver).onChanged(HistoryViewState.NoStopwatchesSoFar)
    }

    @Test
    fun `when stopwatch is clicked, timestamps are shown`() = runBlockingTest {
        val stopwatch1 = Stopwatch(0, "0", 0)
        val stopwatch2 = Stopwatch(1, "1", 1)
        val stopwatches = listOf(stopwatch1, stopwatch2)
        val timestamps = listOf<Timestamp>(mock(), mock())
        whenever(stopwatchRepository.stopwatches()).doReturn(stopwatches)
        whenever(stopwatchRepository.timestamps(stopwatch1.id)).doReturn(timestamps)

        viewModel.initialize()
        viewModel.onStopwatchClick(stopwatch1)

        verify(viewStateObserver).onChanged(
            HistoryViewState.StopwatchesResult(
                stopwatch1,
                timestamps
            )
        )
    }

}