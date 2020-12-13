package org.openhdp.hdt.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.xwray.groupie.*
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.databinding.FragmentHistoryBinding
import org.openhdp.hdt.showText
import org.openhdp.hdt.ui.history.adapter.DayHeaderItem
import org.openhdp.hdt.ui.history.adapter.TimestampEntryItem

@AndroidEntryPoint
class HistoryFragment : Fragment() {


    private val args: HistoryFragmentArgs by navArgs()

    private var popupMenu: PopupMenu? = null

    private val viewModel: HistoryViewModel by viewModels()

    private val adapter = HistoryEntriesAdapter()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val groupieSection = Section()

    private val renderer: (DayHeader) -> Group = {
        val header = DayHeaderItem(it.label, it.isExpanded) {
            viewModel.toggleExpand(it)
        }
        val group = ExpandableGroup(header, it.isExpanded)
        val nestedItems = it.entries.map {
            TimestampEntryItem(it.label)
        }
        group.addAll(nestedItems)
        group
    }

    lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerviewHistory.supportsChangeAnimations = false

        binding.recyclerviewHistory.adapter = groupAdapter.apply {
            add(groupieSection)
        }

        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)

        val stopwatchId = args.stopwatch?.stopwatchId

        viewModel.initialize(stopwatchId)
    }

    private fun renderState(state: HistoryViewState): Any {
        binding.noStopwatches.isVisible = state is HistoryViewState.NoStopwatchesSoFar

        binding.progress.isVisible = state is HistoryViewState.Loading

        return when (state) {
            HistoryViewState.Loading -> {
            }
            HistoryViewState.NoStopwatchesSoFar -> {
                requireActivity().showText("No stopwatches so far")
            }
            is HistoryViewState.Stopwatches -> {
                binding.timerSelector.setOnClickListener {
                    onDropdownClick(it, state.stopwatches)
                }
            }
            is HistoryViewState.NoStopwatchTimestampsSoFar -> {
                binding.timerSelector.text = state.stopwatch.name
                adapter.submitList(emptyList())
                requireActivity().showText("No timestamps so far")
            }
            is HistoryViewState.StopwatchesResult -> {
                binding.timerSelector.text = state.stopwatch.name
                groupieSection.update(state.items.map {
                    renderer.invoke(it)
                })
            }
            is HistoryViewState.Error -> {
                requireActivity().showText("Error ${state.throwable}")
            }
        }
    }

    private fun onDropdownClick(button: View, stopwatches: List<Stopwatch>) {
        popupMenu?.dismiss()
        val menu = PopupMenu(button.context, button)
        stopwatches.forEach { stopwatch ->
            menu.menu.add(stopwatch.name)
        }
        menu.setOnMenuItemClickListener { item ->
            val stopwatch = stopwatches.firstOrNull { it.name == item?.title }

            if (stopwatch != null) {
                viewModel.onStopwatchClick(stopwatch)
            }
            true
        }
        popupMenu = menu
        popupMenu?.show()
    }
}

var RecyclerView.supportsChangeAnimations: Boolean
    get() = (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations ?: false
    set(value) {
        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = value
    }
