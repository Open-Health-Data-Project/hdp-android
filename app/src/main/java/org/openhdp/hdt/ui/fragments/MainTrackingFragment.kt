package org.openhdp.hdt.ui.fragments

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.other.Constants.STOPWATCH_ID
import org.openhdp.hdt.services.StopwatchService
import org.openhdp.hdt.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class MainTrackingFragment : Fragment(R.layout.fragment_main_tracking) {

    private val viewModel: MainViewModel by viewModels()

}