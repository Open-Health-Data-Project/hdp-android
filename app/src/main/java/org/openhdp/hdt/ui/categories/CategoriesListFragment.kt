package org.openhdp.hdt.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.databinding.FragmentCategoriesListBinding
import org.openhdp.hdt.databinding.ItemCategoryBinding
import org.openhdp.hdt.ui.categories.addCategory.AddCategoryBottomSheetFragment
import org.openhdp.hdt.ui.tracking.setupCornersWithColor

@AndroidEntryPoint
class CategoriesListFragment : Fragment(R.layout.fragment_categories_list) {

    private val viewModel: CategoriesListViewModel by viewModels()

    private val radius: Float by lazy {
        resources.getDimension(R.dimen.cardview_corner_size)
    }

    private lateinit var adapter: CategoriesAdapter
    private lateinit var binding: FragmentCategoriesListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)
        binding.fabAddCategory.setOnClickListener { addNewCategory() }
        viewModel.initialize()
    }

    private fun addNewCategory() {
        val bottomSheet = AddCategoryBottomSheetFragment()
        bottomSheet.listener = object : AddCategoryBottomSheetFragment.Listener {

            override fun onAdded(item: Category) {
                viewModel.addNewCategory(item)
            }
        }
        bottomSheet.show(childFragmentManager, "add_category")
    }

    private fun renderState(state: CategoriesViewState) = when (state) {
        CategoriesViewState.Loading -> {
            binding.progress.isVisible = true
        }
        CategoriesViewState.NoCategories -> {
            binding.progress.isVisible = false

            Toast.makeText(
                requireContext(),
                "No categories, click button to add one!",
                Toast.LENGTH_LONG
            ).show()
        }
        is CategoriesViewState.Results -> {
            binding.progress.isVisible = false
            if (binding.recyclerCategories.adapter == null) {
                adapter = CategoriesAdapter(
                    radius = radius,
                    onBrowse = {
                        Toast.makeText(requireContext(), "item $it", Toast.LENGTH_SHORT).show()
                    },
                    onRemove = {
                        Toast.makeText(
                            requireContext(),
                            "Remove not supported in this version",
                            Toast.LENGTH_SHORT
                        ).show()
                        // temporarily delete category
                        // viewModel.deleteCategory(it)
                    }
                )
                binding.recyclerCategories.adapter = adapter
            }
            adapter.submitList(state.categories)
        }
        is CategoriesViewState.Error -> {
            binding.progress.isVisible = false
            Toast.makeText(
                requireContext(),
                "error ${state.issue}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

val diffUtil = object : DiffUtil.ItemCallback<CategoryItem>() {
    override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return when {
            oldItem is CategoryItem.Manual && newItem is CategoryItem.Manual -> {
                oldItem.category.id == newItem.category.id
            }
            oldItem is CategoryItem.Independent && newItem is CategoryItem.Independent -> {
                oldItem.id == newItem.id
            }
            else -> {
                false
            }
        }
    }

    override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
        return when {
            oldItem is CategoryItem.Manual && newItem is CategoryItem.Manual -> {
                oldItem.category.name == newItem.category.name
            }
            oldItem is CategoryItem.Independent && newItem is CategoryItem.Independent -> {
                oldItem.name == newItem.name
            }
            else -> {
                false
            }
        }
    }
}

class CategoriesAdapter(
    val radius: Float,
    val onBrowse: (CategoryItem) -> Unit,
    val onRemove: (CategoryItem) -> Unit
) : ListAdapter<CategoryItem, CategoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(radius, binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = (getItem(position) as? CategoryItem.Manual) ?: return
        holder.bind(item)
        holder.itemView.setOnLongClickListener {
            onRemove.invoke(item)
            true
        }
        holder.itemView.setOnClickListener {
            onBrowse.invoke(item)
        }
    }
}

class CategoryViewHolder(val radius: Float, private val binding: ItemCategoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CategoryItem.Manual) {
        binding.categoryName.text = item.category.name
        binding.categoryName.setupCornersWithColor(item.category.color) {
            setAllCorners(CornerFamily.ROUNDED, radius)
        }
    }
}