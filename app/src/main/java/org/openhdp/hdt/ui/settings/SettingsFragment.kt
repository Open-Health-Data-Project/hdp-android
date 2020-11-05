package org.openhdp.hdt.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.RequestCodes.Companion.EXPORT_STOPWATCHES
import org.openhdp.hdt.RequestCodes.Companion.EXPORT_TIMESTAMPS
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.databinding.FragmentSettingsBinding
import org.openhdp.hdt.showText

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exportStopwatchesButton.setOnClickListener {
            viewModel.onEvent(SettingsEvent.RequestExportStopwatches)
        }
        binding.exportTimestampsButton.setOnClickListener {
            viewModel.onEvent(SettingsEvent.RequestStopwatchPicker)
        }
        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)
    }

    private fun renderState(state: SettingsViewState): Any = when (state) {
        is SettingsViewState.ExportStopwatchesData -> {
            requestExportStopwatches()

        }
        is SettingsViewState.ExportStopwatchTimestamps -> {
            requestExportTimestamps(state.stopwatch)
        }
        is SettingsViewState.DisplayStopwatchPicker -> {
            DialogHelper(requireActivity())
                .showMultiChoice(
                    title = "Select stopwatch",
                    options = state.stopwatches,
                    onPicked = {
                        viewModel.onEvent(SettingsEvent.RequestExportStopwatchTimestamps(it))
                    },
                    renderer = { it.name })
        }
        is SettingsViewState.Error -> {
            requireActivity().showText(state.issue.toString())
        }

    }

    private fun csvIntentOf(name: String): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, name)
        }
    }

    private fun requestExportStopwatches() {
        val intent = csvIntentOf("stopwatches")
        startActivityForResult(intent, EXPORT_STOPWATCHES)
    }

    private fun requestExportTimestamps(stopwatch: Stopwatch) {
        val intent = csvIntentOf("${stopwatch.name}_timestamps")
        startActivityForResult(intent, EXPORT_TIMESTAMPS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data ?: return
        when (requestCode) {
            EXPORT_STOPWATCHES -> {
                viewModel.onEvent(SettingsEvent.ExportStopwatches(uri))
                requireActivity().showText("CSV saved!")
                uri.shareStopwatchData()
            }
            EXPORT_TIMESTAMPS -> {
                viewModel.onEvent(SettingsEvent.ExportStopwatchTimestamps(uri))
                requireActivity().showText("CSV saved!")
                uri.shareStopwatchTimestampsData()
            }
        }
    }

    private fun Uri.shareStopwatchData() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "text/csv";
        intentShareFile.putExtra(Intent.EXTRA_STREAM, this)

        intentShareFile.putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing OHDP stopwatches data"
        )
        startActivity(Intent.createChooser(intentShareFile, "Share CSV"));
    }

    private fun Uri.shareStopwatchTimestampsData() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "text/csv";
        intentShareFile.putExtra(Intent.EXTRA_STREAM, this)

        intentShareFile.putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing OHDP timestamps data"
        )
        startActivity(Intent.createChooser(intentShareFile, "Share CSV"));
    }
}