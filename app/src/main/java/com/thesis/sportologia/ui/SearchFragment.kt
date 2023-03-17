package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentSearchBinding
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.events.ListEventsFragmentSearch
import com.thesis.sportologia.ui.users.ListUsersFragmentUsers
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initSearchBar()
        initContentBlock()
        initNavToProfile()

        return binding.root
    }

    private fun initSearchBar() {
        binding.searchBar.filterButton.setOnClickListener {
            onOpenFilterButtonPressed()
        }
        binding.searchBar.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Called when the user submits the query.
                initSearchSubmission(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Called when the query text is changed by the user.
                initSearchSubmission(newText ?: "")
                return true
            }
        })
    }

    private fun initContentBlock() {
        val fragments = arrayListOf(
            ListUsersFragmentUsers.newInstance(CurrentAccount().id),
            ListServicesFragment(),
            ListEventsFragmentSearch.newInstance(CurrentAccount().id),
        )
        val tabsNames = arrayListOf(
            getString(R.string.search_users),
            getString(R.string.search_services),
            getString(R.string.search_events)
        )
        adapter = PagerAdapter(this, fragments)
        viewPager = binding.pager
        viewPager.adapter = adapter
        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()
    }

    private fun initSearchSubmission(searchText: String) {
        requireActivity().supportFragmentManager.setFragmentResult(
            SUBMIT_SEARCH_QUERY_REQUEST_CODE,
            bundleOf(Pair(SEARCH_QUERY, searchText))
        )
    }

    // навигация к другому пользователю
    private fun initNavToProfile() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo =
                data.getString(USER_ID) ?: return@setFragmentResultListener
            if (userIdToGo != CurrentAccount().id) {
                val direction =
                    SearchFragmentDirections.actionSearchFragmentToProfileNested(userIdToGo)
                findNavController().navigate(
                    direction,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            } else {
                // TODO не переключает вкладку -> крашится. с HomeFragment все нормально
                requireActivity().supportFragmentManager.setFragmentResult(
                    GO_TO_OWN_PROFILE_REQUEST_CODE,
                    bundleOf()
                )
            }
        }
    }

    private fun onOpenFilterButtonPressed() {
        findTopNavController().navigate(R.id.filterFragment,
            null,
            navOptions {
                // https://www.youtube.com/watch?v=lejBUeOSnf8
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

    companion object {
        const val GO_TO_OWN_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_OWN_REQUEST_CODE_FROM_SEARCH"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_USERS"
        const val USER_ID = "USER_ID"

        const val SUBMIT_SEARCH_QUERY_REQUEST_CODE = "SUBMIT_SEARCH_QUERY_REQUEST_CODE"
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }
}