package org.openhdp.hdt.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.ui.viewmodels.CategoriesListViewModel
import org.openhdp.hdt.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class CategoriesListFragment : Fragment(R.layout.fragment_categories_list) {

    private val viewModel: CategoriesListViewModel by viewModels()

}