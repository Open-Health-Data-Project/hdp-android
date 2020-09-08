package org.openhdp.hdt.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.ui.viewmodels.MainViewModel
import org.openhdp.hdt.ui.viewmodels.StopwatchDetailViewModel

@AndroidEntryPoint
class StopwatchDetailFragment : Fragment(R.layout.fragment_stopwatch_detail) {

    private val viewModel: StopwatchDetailViewModel by viewModels()

}