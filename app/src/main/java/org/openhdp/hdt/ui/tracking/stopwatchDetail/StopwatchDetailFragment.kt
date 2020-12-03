package org.openhdp.hdt.ui.tracking.stopwatchDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.FragmentStopwatchDetailBinding
import org.openhdp.hdt.ui.tracking.TrackingFragment

@AndroidEntryPoint
class StopwatchDetailFragment : Fragment() {

    private val viewModel: StopwatchDetailViewModel by viewModels()

    private val navArgs: StopwatchDetailFragmentArgs by navArgs()

    lateinit var binding: FragmentStopwatchDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStopwatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.stopwatchEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }
        binding.stopwatchDetailClose.setOnClickListener {
            exitScreen()
        }
        binding.stopwatchName.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.onStopwatchNameChanged(text?.toString() ?: "")
        })
        binding.stopwatchRemove.setOnClickListener {
            viewModel.delete()
            refreshTrackingScreen()
            exitScreen()
        }
        viewModel.initialize(navArgs.stopwatch)
        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)
    }

    private fun refreshTrackingScreen() {
        val vm = requireActivity()
            .supportFragmentManager
            .fragments
            .filterIsInstance<TrackingFragment>()
            .firstOrNull()?.viewModel
        vm?.initialize()
    }

    private fun exitScreen() {
        findNavController().navigate(StopwatchDetailFragmentDirections.navigateToTrackingFragment())
    }

    private fun renderState(state: StopwatchDetailViewState) = when (state) {
        is StopwatchDetailViewState.Display -> {
            val res = if (state.isInEditMode) {
                R.drawable.ic_check
            } else {
                R.drawable.ic_pencil
            }
            binding.stopwatchName.isEnabled = state.isInEditMode
            if (binding.stopwatchName.text?.toString() != state.stopwatchName) {
                binding.stopwatchName.setText(state.stopwatchName)
            }
            binding.stopwatchEdit.setImageResource(res)
            binding.stopwatchNameLayout.error = state.errorMessage
        }
        StopwatchDetailViewState.Loading -> {
        }
    }
}
