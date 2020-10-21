package org.openhdp.hdt.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.ui.viewmodels.AddElementViewModel

@AndroidEntryPoint
class AddElementFragment : Fragment(R.layout.fragment_add_element) {

    private val viewModel: AddElementViewModel by viewModels()

}