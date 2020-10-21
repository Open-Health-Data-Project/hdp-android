package org.openhdp.hdt.ui.tracking.addCounter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.databinding.FragmentAddElementBinding
import org.openhdp.hdt.ui.base.RoundedSheetDialogFragment
import org.openhdp.hdt.ui.tracking.TrackingItem


@AndroidEntryPoint
class AddElementBottomSheetFragment : RoundedSheetDialogFragment() {

    interface Listener {
        fun onAdded(item: AddStopwatchViewState)
    }

    private var popupMenu: PopupMenu? = null
    var listener: Listener? = null

    private val viewModel: AddStopwatchViewModel by viewModels()

    private lateinit var binding: FragmentAddElementBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddElementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputName.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            val name = text?.toString() ?: ""
            viewModel.onNameChanged(name)
        })
        binding.inputName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requireActivity().hideKeyboard()
                viewModel.onAdded()
                true
            } else {
                false
            }
        }
        binding.buttonAdd.setOnClickListener { viewModel.onAdded() }
        binding.buttonCancel.setOnClickListener { viewModel.onCancel() }
        viewModel.viewState.observe(viewLifecycleOwner, ::renderState)

        viewModel.initialize()
    }

    private fun TextView.setupCategoryPickerWith(categories: List<SelectableCategory>) {
        val selected = categories.firstOrNull { it.selected }?.category
        text = selected?.name ?: "Select category"
        if (selected == null) {
            setTextColor(Color.DKGRAY)
        } else {
            setTextColor(Color.BLACK)
        }
        setOnClickListener { button ->
            popupMenu?.dismiss()
            val menu = PopupMenu(button.context, button)
            categories.forEach { category ->
                menu.menu.add(category.category.name)
            }
            menu.setOnMenuItemClickListener { item ->
                val category = categories.firstOrNull { it.category.name == item?.title }

                if (category != null) {
                    viewModel.onCategoryPicked(category)
                }
                true
            }
            popupMenu = menu
            popupMenu?.show()
        }
    }

    private fun renderState(state: AddStopwatchViewState) {
        if (state.cancelled) {
            dismissAllowingStateLoss()
            return
        }
        if (state.added) {
            listener?.onAdded(state)
            dismissAllowingStateLoss()
            return
        }

        val tintColor = if (state.addButtonEnabled) {
            ContextCompat.getColor(requireContext(), R.color.colorAccent)
        } else {
            ContextCompat.getColor(requireContext(), R.color.inactive)
        }
        binding.buttonAdd.backgroundTintList = ColorStateList.valueOf(tintColor)
        binding.buttonAdd.isEnabled = state.addButtonEnabled
        binding.spinnerCategory.setupCategoryPickerWith(state.categories)
    }
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}