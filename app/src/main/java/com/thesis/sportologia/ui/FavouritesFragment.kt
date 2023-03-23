package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentFavouritesBinding
import com.thesis.sportologia.ui.posts.ListPostsFragmentFavourites
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.events.ListEventsFragment
import com.thesis.sportologia.ui.events.ListEventsFragmentFavourites
import com.thesis.sportologia.ui.services.ListServicesFragmentFavourites
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        initToolbar()
        initContentBlock()
        initNavToProfile()

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.setListener {
            if (it == OnToolbarBasicAction.LEFT) {
                onBackButtonPressed()
            }
        }
    }

    private fun initContentBlock() {
        val tabsFragments = arrayListOf(
            ListPostsFragmentFavourites.newInstance(CurrentAccount().id),
            ListServicesFragmentFavourites.newInstance(CurrentAccount().id),
            ListEventsFragmentFavourites.newInstance(CurrentAccount().id),
        )
        val tabsTitles = arrayListOf(
            getString(R.string.posts),
            getString(R.string.services),
            getString(R.string.events),
        )
        adapter = PagerAdapter(this, tabsFragments)
        viewPager = binding.pager
        viewPager.adapter = adapter

        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsTitles[position]
        }.attach()
    }

    // навигация к другому пользователю
    private fun initNavToProfile() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo = data.getString(USER_ID) ?: return@setFragmentResultListener
            val direction =
                FavouritesFragmentDirections.actionFavouritesFragmentToProfileFragment(userIdToGo)
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

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

    companion object {
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_FAVOURITES"
        const val GO_TO_STATS_REQUEST_CODE = "GO_TO_STATS_REQUEST_CODE_FROM_FAVOURITES"
        const val GO_TO_SERVICE_REQUEST_CODE = "GO_TO_SERVICE_REQUEST_CODE_FROM_FAVOURITES"

        const val USER_ID = "USER_ID"
        const val SERVICE_ID = "SERVICE_ID"
    }
}