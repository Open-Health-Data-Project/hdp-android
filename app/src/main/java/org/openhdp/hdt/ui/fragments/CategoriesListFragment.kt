package org.openhdp.hdt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.FragmentCategoriesListBinding
import org.openhdp.hdt.ui.viewmodels.CategoriesListViewModel
import org.openhdp.hdt.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class CategoriesListFragment : Fragment(R.layout.fragment_categories_list) {

    private val viewModel: CategoriesListViewModel by viewModels()

    private lateinit var binding: FragmentCategoriesListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesListBinding.inflate(inflater, container, false)
        return binding.root
    }
}