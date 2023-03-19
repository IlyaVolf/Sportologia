package com.thesis.sportologia.ui

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
import com.thesis.sportologia.databinding.FragmentFilterEventsBinding
import com.thesis.sportologia.ui.SearchFragment.Companion.FILTER_PARAMETERS
import com.thesis.sportologia.ui.search.adapters.FilterButtonsListAdapter
import com.thesis.sportologia.model.FilterParams
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.ui.search.entities.FilterToggleButtonItem
import com.thesis.sportologia.utils.AssociativeList
import com.thesis.sportologia.utils.Categories
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragmentEvents : Fragment() {

    private lateinit var currentCategories: HashMap<String, Boolean>
    private lateinit var currentFilterParamsEvents: FilterParamsEvents

    private val args by navArgs<FilterFragmentEventsArgs>()

    private lateinit var binding: FragmentFilterEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterEventsBinding.inflate(inflater, container, false)

        currentFilterParamsEvents = getFilterFragmentArg() as FilterParamsEvents
        currentCategories = currentFilterParamsEvents.categories ?: Categories.emptyCategoriesMap

        initCategoriesSpinner()
        initPriceEditText()
        initDistanceEditText()
        initDateIntervalSelector()
        initSortBySpinner()
        initAddressEditText()
        initCloseFilterFragment()

        return binding.root
    }

    private fun getFilterFragmentArg(): FilterParams = args.filterParams

    private fun initDistanceEditText() {
        binding.fragmentFilterEventDistance.filterEdittextTitle.text =
            getString(R.string.filter_distance)
        binding.fragmentFilterEventDistance.filterEdittext.hint =
            getString(R.string.filter_distance_hint)
        binding.fragmentFilterEventDistance.filterEdittext.inputType = InputType.TYPE_CLASS_NUMBER
        binding.fragmentFilterEventDistance.filterEdittextHint.visibility = GONE

        if (currentFilterParamsEvents.distance != null) {
            binding.fragmentFilterEventDistance.filterEdittext
                .setText(currentFilterParamsEvents.distance.toString())
        }
    }

    private fun initPriceEditText() {
        binding.fragmentFilterEventPrice.filterEdittextTitle.text =
            getString(R.string.filter_price)
        binding.fragmentFilterEventPrice.filterEdittext.hint =
            getString(R.string.filter_price_hint)
        binding.fragmentFilterEventPrice.filterEdittext.inputType = InputType.TYPE_CLASS_NUMBER
        binding.fragmentFilterEventPrice.filterEdittextHint.visibility = GONE

        if (currentFilterParamsEvents.price != null) {
            binding.fragmentFilterEventPrice.filterEdittext
                .setText(currentFilterParamsEvents.price!!.toInt().toString())
        }
    }

    private fun initDateIntervalSelector() {
        binding.fragmentFilterEventDate.setDateAndTime(
            currentFilterParamsEvents.dateFrom,
            currentFilterParamsEvents.dateTo
        )
    }

    private fun initAddressEditText() {
        binding.fragmentFilterEventAddress.filterEdittextTitle.text =
            getString(R.string.filter_city)
        binding.fragmentFilterEventAddress.filterEdittext.hint =
            getString(R.string.filter_city_hint)
        binding.fragmentFilterEventAddress.filterEdittext.inputType = InputType.TYPE_CLASS_TEXT
        binding.fragmentFilterEventAddress.filterEdittextHint.visibility = GONE

        if (currentFilterParamsEvents.address != null) {
            binding.fragmentFilterEventAddress.filterEdittext
                .setText(currentFilterParamsEvents.address.toString())
        }
    }

    private fun initSortBySpinner() {
        val options = AssociativeList(
            listOf(
                Pair(getString(R.string.filter_sort_by_date), FilterParamsEvents.EventsSortBy.Date),
                Pair(
                    getString(R.string.filter_sort_by_distance),
                    FilterParamsEvents.EventsSortBy.Distance
                ),
                Pair(
                    getString(R.string.filter_sort_by_popularity),
                    FilterParamsEvents.EventsSortBy.Popularity
                ),
            )
        )

        binding.fragmentFilterEventType.filterSpinnerOnechoiceTitle.text =
            getString(R.string.filter_sort)
        binding.fragmentFilterEventType.filterSpinnerOnechoice.initAdapter(options.getFirsts())
        binding.fragmentFilterEventType.filterSpinnerOnechoice.setListener {
            currentFilterParamsEvents.sortingBy = options.getAssociatedItemSecond(it)
        }

        val currentSortingBy = options.getAssociatedItemFirst(currentFilterParamsEvents.sortingBy)
        binding.fragmentFilterEventType.filterSpinnerOnechoice.setItem(currentSortingBy)
    }

    private fun initCategoriesSpinner() {
        val onCategoryButtonPressed: (FilterToggleButtonItem, Boolean) -> Unit =
            { toggleButton, isPressed ->
                currentCategories[toggleButton.codeText] = isPressed
            }
        val adapter = FilterButtonsListAdapter(onCategoryButtonPressed)
        val filterToggleButtonItemList = mutableListOf<FilterToggleButtonItem>()
        val categoriesKeys = currentCategories.keys.toTypedArray()

        binding.fragmentFilterEventCategories.vfmList.flbList.adapter = adapter
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
        binding.fragmentFilterEventapply.setOnClickListener {
            onApplyButtonPressed()
        }
        binding.fragmentFilterEventCancel.setOnClickListener {
            onCancelButtonPressed()
        }
    }

    private fun onApplyButtonPressed() {
        getLatestParams()

        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf(Pair(FILTER_PARAMETERS, currentFilterParamsEvents))
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
            currentFilterParamsEvents.categories = currentCategories
        } else {
            currentFilterParamsEvents.distance = null
        }

        val distance =
            binding.fragmentFilterEventDistance.filterEdittext.text.toString().toIntOrNull()
        if (distance == null || distance == 0) {
            currentFilterParamsEvents.distance = null
        } else {
            currentFilterParamsEvents.distance =
                binding.fragmentFilterEventDistance.filterEdittext.text.toString().toInt()
        }

        currentFilterParamsEvents.dateFrom = binding.fragmentFilterEventDate.getDateFromMillis()
        currentFilterParamsEvents.dateTo = binding.fragmentFilterEventDate.getDateToMillis()

        val price =
            binding.fragmentFilterEventPrice.filterEdittext.text.toString().toIntOrNull()
        if (price == null) {
            currentFilterParamsEvents.price = null
        } else {
            currentFilterParamsEvents.price =
                binding.fragmentFilterEventPrice.filterEdittext.text.toString().toFloat()
        }

        currentFilterParamsEvents.address = null
    }

}