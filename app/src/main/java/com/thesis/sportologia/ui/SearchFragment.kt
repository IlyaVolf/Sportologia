package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
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
import com.thesis.sportologia.model.FilterParams
import com.thesis.sportologia.model.events.entities.FilterParamsEvents
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.users.entities.FilterParamsUsers
import com.thesis.sportologia.ui.services.ListServicesFragmentSearch
import com.thesis.sportologia.ui.users.ListUsersFragmentSearch
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var searchTabs: List<SearchTab>
    private lateinit var currentSearchTab: SearchTab
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SEARCHH", "onCreate ${savedInstanceState == null}")

        searchTabs =
            (savedInstanceState?.getSerializable("searchTabs") as SearchTabList?)?.searchTabList
                ?: listOf(
                    SearchTab(
                        SearchTab.Tab.USERS,
                        ListUsersFragmentSearch.newInstance(CurrentAccount().id),
                        { filterParams ->
                            TabsFragmentDirections.actionTabsFragmentToFilterFragmentUsers(
                                filterParams
                            )
                        },
                        getString(R.string.search_users),
                        SUBMIT_SEARCH_USERS_QUERY_REQUEST_CODE,
                        FilterParamsUsers.newEmptyInstance()
                    ),
                    SearchTab(
                        SearchTab.Tab.SERVICES,
                        ListServicesFragmentSearch.newInstance(CurrentAccount().id),
                        { filterParams ->
                            TabsFragmentDirections.actionTabsFragmentToFilterFragmentServices(
                                filterParams
                            )
                        },
                        getString(R.string.search_services),
                        SUBMIT_SEARCH_SERVICES_QUERY_REQUEST_CODE,
                        FilterParamsServices.newEmptyInstance()
                    ),
                    SearchTab(
                        SearchTab.Tab.EVENTS,
                        ListEventsFragmentSearch.newInstance(CurrentAccount().id),
                        { filterParams ->
                            TabsFragmentDirections.actionTabsFragmentToFilterFragmentEvents(
                                filterParams
                            )
                        },
                        getString(R.string.search_events),
                        SUBMIT_SEARCH_EVENTS_QUERY_REQUEST_CODE,
                        FilterParamsEvents.newEmptyInstance()
                    )
                )
        currentSearchTab = searchTabs[0]

        Log.d("SEARCHH", "onCreate ${currentSearchTab.filterParams}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("searchTabs", SearchTabList(searchTabs))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        Log.d("SEARCHH", "onCreateView ${currentSearchTab.filterParams}")

        initSearchBar()
        initFilterResultListener()
        initContentBlock()
        initOnInfoPressed()
        initNavToProfile()

        //sendSearchQuery()

        return binding.root
    }

    private fun initSearchBar() {
        binding.searchBar.filterButton.setOnClickListener {
            onOpenFilterButtonPressed()
        }
        binding.searchBar.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            // Called when the user submits the query.
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.searchBar.searchView.clearFocus()
                searchQuery = query
                Log.d("ABCDEF", "onQueryTextSubmit")
                sendSearchQuery()
                return true
            }

            // Called when the query text is changed by the user.
            override fun onQueryTextChange(newText: String?): Boolean {
                if (searchQuery == newText) return true
                searchQuery = newText ?: ""
                Log.d("ABCDEF", "onQueryTextChange")
                sendSearchQuery()
                return true
            }
        })
    }

    private fun initContentBlock() {
        adapter = PagerAdapter(this, ArrayList(searchTabs.map { it.tabFragment }))
        viewPager = binding.pager
        viewPager.adapter = adapter
        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = searchTabs[position].tabName
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentSearchTab = searchTabs[tab?.position ?: 0]
             }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initFilterResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            FILTER_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val filterParams =
                data.getSerializable(FILTER_PARAMETERS) ?: return@setFragmentResultListener
            currentSearchTab.filterParams = filterParams as FilterParams
            sendSearchQuery()
        }
    }

    private fun sendSearchQuery() {
        requireActivity().supportFragmentManager.setFragmentResult(
            currentSearchTab.requestCode,
            bundleOf(
                Pair(SEARCH_QUERY, searchQuery),
                Pair(FILTER_PARAMETERS, currentSearchTab.filterParams)
            )
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
                requireActivity().supportFragmentManager.setFragmentResult(
                    GO_TO_OWN_PROFILE_REQUEST_CODE,
                    bundleOf()
                )
            }
        }
    }

    private fun initOnInfoPressed() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_SERVICE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val serviceId = data.getLong(SERVICE_ID)
            val direction =
                SearchFragmentDirections.actionSearchFragmentToService(
                    serviceId
                )
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
        }
    }

    private fun onOpenFilterButtonPressed() {
        findTopNavController().navigate(currentSearchTab.direction(currentSearchTab.filterParams),
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

    data class SearchTabList(
        val searchTabList: List<SearchTab>
    ) : java.io.Serializable

    data class SearchTab(
        val tab: Tab,
        val tabFragment: Fragment,
        val direction: (FilterParams) -> NavDirections,
        val tabName: String,
        val requestCode: String,
        var filterParams: FilterParams
    ) {
        enum class Tab {
            USERS, SERVICES, EVENTS
        }
    }

    companion object {
        const val GO_TO_OWN_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_OWN_REQUEST_CODE_FROM_SEARCH"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_SEARCH"
        const val GO_TO_STATS_REQUEST_CODE = "GO_TO_STATS_REQUEST_CODE_FROM_USERS"
        const val GO_TO_SERVICE_REQUEST_CODE = "GO_TO_SERVICE_REQUEST_CODE_FROM_USERS"

        const val USER_ID = "USER_ID"
        const val SERVICE_ID = "SERVICE_ID"

        const val SUBMIT_SEARCH_USERS_QUERY_REQUEST_CODE = "SUBMIT_SEARCH_USERS_QUERY_REQUEST_CODE"
        const val SUBMIT_SEARCH_SERVICES_QUERY_REQUEST_CODE =
            "SUBMIT_SEARCH_SERVICES_QUERY_REQUEST_CODE"
        const val SUBMIT_SEARCH_EVENTS_QUERY_REQUEST_CODE =
            "SUBMIT_SEARCH_EVENTS_QUERY_REQUEST_CODE"
        const val SEARCH_QUERY = "SEARCH_QUERY"

        const val FILTER_REQUEST_CODE = "FILTER_REQUEST_CODE"
        const val FILTER_PARAMETERS = "FILTER_PARAMETERS"

        const val UPDATE_LIST_REQUEST_CODE = "UPDATE_LIST_REQUEST_CODE"
        const val UPDATE_LIST = "UPDATE_LIST"
    }
}