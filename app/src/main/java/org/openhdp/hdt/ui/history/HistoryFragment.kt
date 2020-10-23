package org.openhdp.hdt.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.databinding.FragmentHistoryBinding
import timber.log.Timber
import java.text.SimpleDateFormat

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var popupMenu: PopupMenu? = null

    private val viewModel: HistoryViewModel by viewModels()

    private val adapter = HistoryEntriesAdapter()

    lateinit var binding: FragmentHistoryBinding

    private val EXPORT_REQ_CODE = 2

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

        binding.recyclerviewHistory.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)

        viewModel.initialize()
    }

    private fun renderState(state: HistoryViewState) {
        binding.noStopwatches.isVisible = state is HistoryViewState.NoStopwatchesSoFar
        when (state) {
            HistoryViewState.Loading -> {

            }
            HistoryViewState.NoStopwatchesSoFar -> {

            }
            is HistoryViewState.Stopwatches -> {
                binding.exportButton.isEnabled = false
                binding.timerSelector.setOnClickListener {
                    onClick(it, state.stopwatches)
                }
            }
            is HistoryViewState.NoStopwatchesTimestampsSoFar -> {
                binding.exportButton.isEnabled = false

                binding.timerSelector.text = state.stopwatch.name
                adapter.submitList(emptyList())
                Toast.makeText(
                    requireActivity().applicationContext,
                    "No timestamps so far",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is HistoryViewState.StopwatchesResult -> {
                binding.timerSelector.text = state.stopwatch.name
                adapter.submitList(state.timestamps)
                binding.exportButton.isEnabled = true
                binding.exportButton.setOnClickListener { button ->
                    //viewModel.onExportClick(state.stopwatch)
                    fileExport(state.stopwatch)
                }
            }
            is HistoryViewState.Error -> {

            }
        }
    }

    private fun fileExport(stopwatch: Stopwatch) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_TITLE, "export_${stopwatch.name}")
        startActivityForResult(intent, EXPORT_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        val fileUri = resultData?.data
        if (fileUri != null && requestCode == EXPORT_REQ_CODE) {
            val stopWatchName = viewModel.doExport(fileUri)
            Toast.makeText(requireActivity(), "CSV saved!", Toast.LENGTH_SHORT).show()

            if (stopWatchName != null) {
                fileUri.share(stopWatchName)
            }
        } else {
            Timber.d("uri is null...")
        }
    }

    private fun Uri.share(stopwatchName: String) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "text/csv";
        intentShareFile.putExtra(Intent.EXTRA_STREAM, this)

        intentShareFile.putExtra(
            Intent.EXTRA_SUBJECT,
            "Sharing OHDP - \'$stopwatchName\' stopwatch data"
        );
        intentShareFile.putExtra(
            Intent.EXTRA_TEXT,
            "Hello, I'd like to share new timestamps regarding stopwatch \'$stopwatchName\'"
        )
        startActivity(Intent.createChooser(intentShareFile, "Share CSV"));
    }

    private fun onClick(button: View, stopwatches: List<Stopwatch>) {
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
