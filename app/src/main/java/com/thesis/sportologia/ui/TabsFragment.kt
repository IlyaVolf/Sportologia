package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentTabsBinding
import com.thesis.sportologia.ui.home.HomeFragment
import com.thesis.sportologia.ui.search.search_screen.SearchFragment
import com.thesis.sportologia.ui.services.service_screen.ServicesAcquiredFragment

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private var _binding: FragmentTabsBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTabsBinding.bind(view)

        val navHost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        binding.bottomNavigationView.itemIconTintList = null

        requireActivity().supportFragmentManager.setFragmentResultListener(
            HomeFragment.GO_TO_OWN_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, _ ->
            binding.bottomNavigationView.selectedItemId = R.id.profile_own
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            SearchFragment.GO_TO_OWN_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, _ ->
            binding.bottomNavigationView.selectedItemId = R.id.profile_own
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            ServicesAcquiredFragment.GO_TO_OWN_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, _ ->
            binding.bottomNavigationView.selectedItemId = R.id.profile_own
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
