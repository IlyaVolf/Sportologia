package com.thesis.sportologia.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thesis.sportologia.ui.ListEventsFragment
import com.thesis.sportologia.ui.ListPostsFragment
import com.thesis.sportologia.ui.ListServicesFragment


class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> ListPostsFragment()
            1 -> ListServicesFragment()
            2 -> ListEventsFragment()
            else -> throw Exception()
        }
    }
}