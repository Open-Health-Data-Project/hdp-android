package org.openhdp.hdt.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.databinding.FragmentTrackingBinding
import timber.log.Timber


@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: TrackingViewModel by viewModels(ownerProducer = { requireActivity() })

    private lateinit var binding: FragmentTrackingBinding
    private lateinit var adapter: DashboardItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGridView()
        setupAddTimerButton()

        viewModel.initialize()

        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)
    }

    private fun setupAddTimerButton() {
        binding.addNewTimer.setOnClickListener {

        }
    }

    private fun setupGridView() {
        val touchHelper = ItemTouchHelper(itemTouchCallback())

        adapter = DashboardItemsAdapter().apply {
            listener = object : OnItemClickListener {
                override fun toggleTimer(item: TrackingItem) {
                    viewModel.toggleTimer(item)
                }

                override fun onSettingsClick(item: TrackingItem) {
                    viewModel.onSettingsClick(item)
                }
            }
            dragChangeListener = object : OnDragChangeListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    viewModel.onDragStarted()
                    touchHelper.startDrag(viewHolder)
                }

                override fun onEndDrag() {
                    viewModel.onDragEnded()
                }
            }
        }
        touchHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

    }

    private fun renderState(state: TrackingViewState) {
        Timber.d("renderState $state")
        when (state) {
            TrackingViewState.Loading -> {

            }
            is TrackingViewState.Results -> {
                adapter.items = (state.items)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun itemTouchCallback() = object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or
                    ItemTouchHelper.START or
                    ItemTouchHelper.END
            val swipeFlags = ACTION_STATE_IDLE
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Timber.w("onMove $viewHolder, $target")
            val firstItem = viewHolder.asDashboardItem() ?: return false
            val otherItem = target.asDashboardItem() ?: return false
            viewModel.reorder(firstItem, otherItem)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

    }
}
