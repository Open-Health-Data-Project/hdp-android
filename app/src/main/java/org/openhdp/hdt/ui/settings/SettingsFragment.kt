package org.openhdp.hdt.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.RequestCodes.Companion.EXPORT_TIMESTAMPS
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.databinding.FragmentSettingsBinding
import org.openhdp.hdt.showText
import org.openhdp.hdt.ui.settings.export.ExportPickerHelper
import java.util.*

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

        binding.exportButton.setOnClickListener {
            viewModel.onEvent(SettingsEvent.RequestGenericExportDialog)
        }
        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)
        viewModel.initialize()
    }

    private fun renderState(state: SettingsViewState): Any = when (state) {
        is SettingsViewState.Error -> {
            requireActivity().showText(state.issue.toString())
        }
        is SettingsViewState.Display -> {
            renderDisplayState(state)
        }
        is SettingsViewState.DisplayExportPicker -> {
            ExportPickerHelper(requireActivity()).displayExportPicker(
                state.stopwatches,
                ::exportStopwatches,
                ::exportCategories,
                ::exportTimestamps,
                ::exportAllTimestamps
            )
        }
        is SettingsViewState.Share -> {
            state.uri.share()
        }
    }

    private fun renderDisplayState(state: SettingsViewState.Display) {
        val hours = state.hours
        val minutes = state.minutes
        binding.startOfDayLabel.text = String.format(
            Locale.US,
            "%02d:%02d",
            hours,
            minutes
        )
        binding.startOfDayButton.setOnClickListener {
            val dialog = TimePickerDialog(
                requireActivity(),
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        viewModel.onEvent(SettingsEvent.ChangeStartOfDay(hourOfDay, minute))
                    }
                },
                hours,
                minutes,
                true
            )
            dialog.show()
        }
    }

    private fun exportStopwatches() {
        viewModel.onEvent(SettingsEvent.ExportStopwatches)
    }

    private fun exportCategories() {
        viewModel.onEvent(SettingsEvent.ExportCategories)
    }

    private fun exportTimestamps(stopwatch: Stopwatch) {
        viewModel.onEvent(SettingsEvent.ExportTimestamps(stopwatch))
    }
    private fun exportAllTimestamps() {
        viewModel.onEvent(SettingsEvent.ExportAllTimestamps)
    }

    private fun csvIntentOf(name: String): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, name)
        }
    }

    private fun requestExportTimestamps(stopwatch: Stopwatch) {
        val intent = csvIntentOf("${stopwatch.name}_timestamps")
        startActivityForResult(intent, EXPORT_TIMESTAMPS)
    }

    private fun Uri.share() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "text/csv";
        intentShareFile.putExtra(Intent.EXTRA_STREAM, this)

        intentShareFile.putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing OHDP data"
        )
        startActivity(Intent.createChooser(intentShareFile, "Share CSV"));
    }
}