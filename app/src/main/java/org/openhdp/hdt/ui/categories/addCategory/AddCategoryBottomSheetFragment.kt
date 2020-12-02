package org.openhdp.hdt.ui.categories.addCategory

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.databinding.FragmentAddCategoryBinding
import org.openhdp.hdt.databinding.ItemColorToPickBinding
import org.openhdp.hdt.ui.base.RoundedSheetDialogFragment
import org.openhdp.hdt.ui.tracking.addCounter.hideKeyboard


@AndroidEntryPoint
class AddCategoryBottomSheetFragment : RoundedSheetDialogFragment() {

    companion object {

        private const val ARG_CATEGORY = "ARG_CATEGORY"

        @JvmStatic
        fun newInstance(category: Category? = null): AddCategoryBottomSheetFragment {
            return AddCategoryBottomSheetFragment().apply {
                arguments = bundleOf(ARG_CATEGORY to category)
            }
        }
    }

    interface Listener {
        fun onAdded(item: Category)
    }

    var listener: Listener? = null

    lateinit var adapter: ColorPickerAdapter

    private val viewModel: AddCategoryViewModel by viewModels()
    private var category: Category? = null

    private lateinit var binding: FragmentAddCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = requireArguments().getSerializable(ARG_CATEGORY) as? Category


        binding.inputName.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            val name = text?.toString() ?: ""
            viewModel.onNameChanged(name)
        })
        binding.inputName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requireActivity().hideKeyboard()
                viewModel.onAdded()
                true
            }
            false
        }
        binding.buttonAdd.setOnClickListener { viewModel.onAdded() }
        binding.buttonCancel.setOnClickListener { viewModel.onCancel() }

        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)

        viewModel.initialize()

        category?.let {
            val name = it.name
            binding.inputName.setText(name)
            viewModel.onPicked(SelectableColor(it.color, selected = true))
        }

        binding.buttonAdd.setText(
            if (category == null) {
                R.string.new_counter_label_create
            } else {
                R.string.new_counter_label_update
            }
        )
    }

    private fun renderState(state: AddCategoryViewState) {
        if (state.isLoading) {

        }
        if (state.cancelled) {
            dismissAllowingStateLoss()
            return
        }
        if (state.added) {
            val category = category
            val updatedCategory = category?.copy(name = state.name, color = state.currentColor())
                ?: state.asCategory()
            listener?.onAdded(updatedCategory)
            dismissAllowingStateLoss()
            return
        }
        if (binding.recyclerCategories.adapter == null) {
            adapter = ColorPickerAdapter(viewModel::onPicked)
            binding.recyclerCategories.adapter = adapter
        }
        adapter.items = state.colors

        val tint = if (state.addButtonEnabled) {
            R.color.colorAccent
        } else {
            R.color.inactive
        }
        binding.buttonAdd.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), tint))
        binding.buttonAdd.isEnabled = state.addButtonEnabled
    }
}

class ColorPickerAdapter(private val listener: (item: SelectableColor) -> Unit) :
    RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

    var items: List<SelectableColor> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding =
            ItemColorToPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.invoke(item)

        }
    }

    class ColorViewHolder(private val binding: ItemColorToPickBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectableColor) {
            if (item.selected) {
                binding.root.setBackgroundColor(Color.BLACK)
            } else {
                binding.root.setBackgroundColor(item.color)
            }
            binding.view.setBackgroundColor(item.color)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}