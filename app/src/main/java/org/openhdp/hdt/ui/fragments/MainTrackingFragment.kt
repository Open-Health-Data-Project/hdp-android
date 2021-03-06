package org.openhdp.hdt.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class MainTrackingFragment : Fragment(R.layout.fragment_main_tracking) {

    private val viewModel: MainViewModel by viewModels()

}