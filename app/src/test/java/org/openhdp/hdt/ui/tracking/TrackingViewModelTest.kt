package org.openhdp.hdt.ui.tracking

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
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
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.settings.StartOfDay
import org.openhdp.hdt.ui.settings.StartOfDayUseCase
import java.lang.IllegalStateException

@RunWith(MockitoJUnitRunner::class)
class TrackingViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()


    lateinit var viewModel: TrackingViewModel

    val stopwatchRepository = mock<StopwatchRepository>()
    val startOfDayUseCase = mock<StartOfDayUseCase>()
    val mapper = mock<TrackingItemsMapper>()
    val viewStateObserver = mock<Observer<TrackingViewState>>()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TrackingViewModel(stopwatchRepository, startOfDayUseCase, mapper)
        viewModel.viewState.observeForever(viewStateObserver)
    }

    @After
    fun after() {
        viewModel.viewState.removeObserver(viewStateObserver)
        Dispatchers.resetMain()
    }

    @Test
    fun `renders stopwatches on initialization`() = runBlockingTest() {
        val stopwatch1 = Stopwatch(0, "1", "1")
        val stopwatch2 = Stopwatch(1, "2", "1")
        val category1 = Category(1, "red", 1, Color.RED)
        val category2 = Category(2, "blue", 2, Color.BLUE)
        val startOfDay = StartOfDay(0, 0)

        val stopwatches = listOf(stopwatch1, stopwatch2)
        val categories = listOf(category1, category2)

        whenever(stopwatchRepository.stopwatches()).doReturn(stopwatches)
        whenever(stopwatchRepository.categories()).doReturn(categories)
        whenever(startOfDayUseCase.getCurrentStartOfDay()).doReturn(startOfDay)
        whenever(mapper.toTrackingItems(stopwatches, categories, startOfDay))
            .doReturn(listOf(mock<TrackingItem>(), mock<TrackingItem>()))

        viewModel.initialize()

        verify(viewStateObserver).onChanged(any<TrackingViewState.Results>())
    }

    @Test
    fun `renders error on initialization`() = runBlockingTest {
        whenever(stopwatchRepository.stopwatches()).thenThrow(IllegalStateException())

        viewModel.initialize()

        verify(viewStateObserver).onChanged(any<TrackingViewState.Error>())
    }
}