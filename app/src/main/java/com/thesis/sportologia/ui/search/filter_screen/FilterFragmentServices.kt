package com.thesis.sportologia.ui.search.filter_screen

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentFilterServicesBinding
import com.thesis.sportologia.ui.search.search_screen.SearchFragment.Companion.FILTER_PARAMETERS
import com.thesis.sportologia.ui.search.adapters.FilterButtonsListAdapter
import com.thesis.sportologia.model.FilterParams
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.search.search_screen.SearchFragment
import com.thesis.sportologia.ui.search.entities.FilterToggleButtonItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.AssociativeList
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.formatFloat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragmentServices : Fragment() {

    private lateinit var currentCategories: HashMap<String, Boolean>
    private lateinit var currentFilterParamsServices: FilterParamsServices

    private val args by navArgs<FilterFragmentServicesArgs>()

    private var _binding: FragmentFilterServicesBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterServicesBinding.inflate(inflater, container, false)

        currentFilterParamsServices = getFilterFragmentArg() as FilterParamsServices
        currentCategories = currentFilterParamsServices.categories ?: Categories.emptyCategoriesMap

        initResetButton()
        initCategoriesSpinner()
        initPriceEditText()
        initRatingEditText()
        initServiceTypeSpinner()
        initSortBySpinner()
        initCloseFilterFragment()

        return binding.root
    }

    private fun getFilterFragmentArg(): FilterParams = args.filterParams

    private fun initResetButton() {
        binding.fragmentFilterServiceTb.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> {
                    /*currentFilterParamsServices = getFilterFragmentArg() as FilterParamsServices
                    currentCategories = currentFilterParamsServices.categories ?: Categories.emptyCategoriesMap*/
                }
                else -> {}
            }
        }
    }

    private fun initPriceEditText() {
        binding.fragmentFilterServicePrice.filterEdittextTitle.text =
            getString(R.string.filter_price)
        binding.fragmentFilterServicePrice.filterEdittext.hint =
            getString(R.string.filter_price_hint)
        binding.fragmentFilterServicePrice.filterEdittext.inputType = InputType.TYPE_CLASS_NUMBER
        binding.fragmentFilterServicePrice.filterEdittextHint.visibility = GONE

        if (currentFilterParamsServices.priceFrom != null) {
            binding.fragmentFilterServicePrice.filterEdittext
                .setText(currentFilterParamsServices.priceFrom!!.toInt().toString())
        }
    }

    private fun initRatingEditText() {
        binding.fragmentFilterServiceRating.filterEdittextTitle.text =
            getString(R.string.filter_rating)
        binding.fragmentFilterServiceRating.filterEdittext.hint =
            getString(R.string.filter_rating_hint)
        binding.fragmentFilterServiceRating.filterEdittext.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.fragmentFilterServiceRating.filterEdittextHint.visibility = GONE

        if (currentFilterParamsServices.ratingFrom != null) {
            binding.fragmentFilterServiceRating.filterEdittext
                .setText(formatFloat(currentFilterParamsServices.ratingFrom!!, 1, false))
        }
    }

    private fun initServiceTypeSpinner() {
        val options = com.thesis.sportologia.utils.AssociativeList(
            listOf(
                Pair(
                    getString(R.string.filter_services_all),
                    null
                ),
                Pair(
                    getString(R.string.filter_services_tr_programs_short),
                    ServiceType.TRAINING_PROGRAM
                ),
            )
        )

        binding.fragmentFilterServiceType.filterSpinnerOnechoiceTitle.text =
            getString(R.string.filter_sort)
        binding.fragmentFilterServiceType.filterSpinnerOnechoice.initAdapter(options.getFirsts())
        binding.fragmentFilterServiceType.filterSpinnerOnechoice.setListener {
            currentFilterParamsServices.serviceType = options.getAssociatedItemSecond(it)
        }

        val currentServiceType =
            options.getAssociatedItemFirst(currentFilterParamsServices.serviceType)
        binding.fragmentFilterServiceType.filterSpinnerOnechoice.setItem(currentServiceType)
    }

    private fun initSortBySpinner() {
        val options = AssociativeList(
            listOf(
                Pair(
                    getString(R.string.filter_sort_by_popularity),
                    FilterParamsServices.ServicesSortBy.Popularity
                ),
                Pair(
                    getString(R.string.filter_sort_by_rating),
                    FilterParamsServices.ServicesSortBy.Rating
                ),
                Pair(
                    getString(R.string.filter_sort_by_price),
                    FilterParamsServices.ServicesSortBy.Price
                ),
            )
        )

        binding.fragmentFilterServiceSortBy.filterSpinnerOnechoiceTitle.text =
            getString(R.string.filter_sort)
        binding.fragmentFilterServiceSortBy.filterSpinnerOnechoice.initAdapter(options.getFirsts())
        binding.fragmentFilterServiceSortBy.filterSpinnerOnechoice.setListener {
            currentFilterParamsServices.sortBy = options.getAssociatedItemSecond(it)
        }

        val currentSortingBy = options.getAssociatedItemFirst(currentFilterParamsServices.sortBy)
        binding.fragmentFilterServiceSortBy.filterSpinnerOnechoice.setItem(currentSortingBy)
    }

    private fun initCategoriesSpinner() {
        val onCategoryButtonPressed: (FilterToggleButtonItem, Boolean) -> Unit =
            { toggleButton, isPressed ->
                currentCategories[toggleButton.codeText] = isPressed
            }
        val adapter = FilterButtonsListAdapter(onCategoryButtonPressed)
        val filterToggleButtonItemList = mutableListOf<FilterToggleButtonItem>()
        val categoriesKeys = currentCategories.keys.toTypedArray()

        binding.fragmentFilterServiceCategories.vfmList.flbList.adapter = adapter
        for (i in categoriesKeys.indices) {
            filterToggleButtonItemList.add(
                FilterToggleButtonItem(
                    categoriesKeys[i],
                    Categories.convertEnumToCategory(context!!, categoriesKeys[i])!!,
                    i,
                    currentCategories[categoriesKeys[i]]!!
                )
            )
        }
        adapter.setupItems(filterToggleButtonItemList)
    }

    private fun initCloseFilterFragment() {
        binding.fragmentFilterServiceapply.setOnClickListener {
            onApplyButtonPressed()
        }
        binding.fragmentFilterServiceCancel.setOnClickListener {
            onCancelButtonPressed()
        }
    }

    private fun onApplyButtonPressed() {
        getLatestParams()

        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf(Pair(FILTER_PARAMETERS, currentFilterParamsServices))
        )
        findNavController().popBackStack()
    }

    private fun onCancelButtonPressed() {
        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf(Pair(FILTER_PARAMETERS, null))
        )
        findNavController().popBackStack()
    }

    private fun getLatestParams() {
        if (currentCategories.containsValue(true)) {
            currentFilterParamsServices.categories = currentCategories
        } else {
            currentFilterParamsServices.categories = null
        }

        val priceFrom =
            binding.fragmentFilterServicePrice.filterEdittext.text.toString().toIntOrNull()
        if (priceFrom == null) {
            currentFilterParamsServices.priceFrom = null
        } else {
            currentFilterParamsServices.priceFrom =
                binding.fragmentFilterServicePrice.filterEdittext.text.toString().toFloat()
        }

        val ratingFrom =
            binding.fragmentFilterServiceRating.filterEdittext.text.toString().toFloatOrNull()
        if (ratingFrom == null || !validateRating(ratingFrom)) {
            currentFilterParamsServices.ratingFrom = null
        } else {
            currentFilterParamsServices.ratingFrom =
                binding.fragmentFilterServiceRating.filterEdittext.text.toString().toFloat()
        }

    }

    private fun validateRating(rating: Float): Boolean {
        return rating in 1.0..5.0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}