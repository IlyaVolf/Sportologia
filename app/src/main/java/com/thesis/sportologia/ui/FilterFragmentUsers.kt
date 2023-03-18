package com.thesis.sportologia.ui

import android.location.Address
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
import com.thesis.sportologia.databinding.FragmentFilterUsersBinding
import com.thesis.sportologia.ui.SearchFragment.Companion.FILTER_PARAMETERS
import com.thesis.sportologia.ui.search.adapters.FilterButtonsListAdapter
import com.thesis.sportologia.model.FilterParams
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.search.entities.FilterToggleButtonItem
import com.thesis.sportologia.utils.Categories
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragmentUsers : Fragment() {

    private lateinit var currentCategories: HashMap<String, Boolean>
    private lateinit var currentFilterParamsUsers: FilterParamsUsers

    private val args by navArgs<FilterFragmentUsersArgs>()

    private lateinit var binding: FragmentFilterUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterUsersBinding.inflate(inflater, container, false)

        currentFilterParamsUsers = getFilterFragmentArg() as FilterParamsUsers
        currentCategories = currentFilterParamsUsers.categories ?: Categories.emptyCategoriesMap

        initDistanceEditText()
        initAddressEditText()
        initUserTypeSpinner()
        initAdapter()
        initCloseFilterFragment()

        return binding.root
    }

    private fun getFilterFragmentArg(): FilterParams = args.filterParams

    private fun initDistanceEditText() {
        binding.fragmentFilterUserDistance.filterEdittextTitle.text =
            getString(R.string.filter_distance)
        binding.fragmentFilterUserDistance.filterEdittext.hint =
            getString(R.string.filter_distance_hint)
        binding.fragmentFilterUserDistance.filterEdittext.inputType = InputType.TYPE_CLASS_NUMBER
        binding.fragmentFilterUserDistance.filterEdittextHint.visibility = GONE

        if (currentFilterParamsUsers.distance != null) {
            binding.fragmentFilterUserDistance.filterEdittext
                .setText(currentFilterParamsUsers.distance.toString())
        }
    }

    private fun initAddressEditText() {
        binding.fragmentFilterUserCity.filterEdittextTitle.text = getString(R.string.filter_city)
        binding.fragmentFilterUserCity.filterEdittext.hint = getString(R.string.filter_city_hint)
        binding.fragmentFilterUserCity.filterEdittext.inputType = InputType.TYPE_CLASS_TEXT
        binding.fragmentFilterUserCity.filterEdittextHint.visibility = GONE

        if (currentFilterParamsUsers.address != null) {
            binding.fragmentFilterUserCity.filterEdittext
                .setText(currentFilterParamsUsers.address.toString())
        }
    }

    private fun initUserTypeSpinner() {
        val options = listOf(
            getString(R.string.search_all),
            getString(R.string.search_athletes),
            getString(R.string.search_organizations)
        )
        binding.fragmentFilterUserType.filterSpinnerOnechocie.initAdapter(options)
        binding.fragmentFilterUserType.filterSpinnerOnechocie.setListener {
            when (it) {
                options[0] -> currentFilterParamsUsers.isAthTOrgF = null
                options[1] -> currentFilterParamsUsers.isAthTOrgF = true
                options[2] -> currentFilterParamsUsers.isAthTOrgF = false
            }
        }

        val currentType = when (currentFilterParamsUsers.isAthTOrgF) {
            null -> options[0]
            true -> options[1]
            false -> options[2]
        }
        binding.fragmentFilterUserType.filterSpinnerOnechocie.setItem(currentType)
    }

    private fun initAdapter() {
        val onCategoryButtonPressed: (FilterToggleButtonItem, Boolean) -> Unit =
            { toggleButton, isPressed ->
                currentCategories[toggleButton.codeText] = isPressed
            }
        val adapter = FilterButtonsListAdapter(onCategoryButtonPressed)
        val filterToggleButtonItemList = mutableListOf<FilterToggleButtonItem>()
        val categoriesKeys = currentCategories.keys.toTypedArray()

        binding.fragmentFilterUserCategories.vfmList.flbList.adapter = adapter
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
        binding.fragmentFilterUserapply.setOnClickListener {
            onApplyButtonPressed()
        }
        binding.fragmentFilterUserCancel.setOnClickListener {
            onCancelButtonPressed()
        }
    }

    private fun onApplyButtonPressed() {
        getLatestParams()

        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf(Pair(FILTER_PARAMETERS, currentFilterParamsUsers))
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
            currentFilterParamsUsers.categories = currentCategories
        } else {
            currentFilterParamsUsers.distance = null
        }

        val distance =
            binding.fragmentFilterUserDistance.filterEdittext.text.toString().toIntOrNull()
        if (distance == null || distance == 0) {
            currentFilterParamsUsers.distance = null
        } else {
            currentFilterParamsUsers.distance =
                binding.fragmentFilterUserDistance.filterEdittext.text.toString().toInt()
        }

        currentFilterParamsUsers.address = null
    }

}