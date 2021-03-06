package org.openhdp.hdt.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.ui.viewmodels.CategoriesDetailViewModel
import org.openhdp.hdt.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class CategoriesDetailFragment : Fragment(R.layout.fragment_categories_detail) {

    private val viewModel: CategoriesDetailViewModel by viewModels()

}