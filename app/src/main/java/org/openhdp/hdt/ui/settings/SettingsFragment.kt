package org.openhdp.hdt.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.BuildConfig
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.databinding.FragmentSettingsBinding
import org.openhdp.hdt.showText
import org.openhdp.hdt.ui.settings.export.ExportPickerHelper
import org.openhdp.hdt.widget.AppWidgetConfigActivity
import java.util.*

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        const val ACTION_PIN_APP_WIDGET = "ACTION_PIN_APP_WIDGET"
        const val EXTRA_STOPWATCH_ID = "EXTRA_STOPWATCH_ID"
    }

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.versionName.text = "Version name: \"${BuildConfig.VERSION_NAME}\""
        binding.versionCode.text = "Version code: ${BuildConfig.VERSION_CODE}"

        val widgetPinningSupported = widgetPinningSupported()
        if (widgetPinningSupported) {
            binding.enableStopwatchWidgetRoot.setOnClickListener { view1 ->
                AlertDialog.Builder(requireContext())
                    .setTitle("How to configure stopwatch?")
                    .setMessage("Long press on home screen and select widget!")
                    .setPositiveButton("Got it!") { _, _ -> }
                    .show()
            }
        }
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

    private fun widgetPinningSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}