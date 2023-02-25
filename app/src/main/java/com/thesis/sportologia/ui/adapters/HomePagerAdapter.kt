package com.thesis.sportologia.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thesis.sportologia.ui.ListEventsFragment
import com.thesis.sportologia.ui.ListPostsFragment
import com.thesis.sportologia.ui.ListServicesFragment


class HomePagerAdapter(fragment: Fragment, val fragments: ArrayList<Fragment>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> fragments[0]
            1 -> fragments[1]
            2 -> fragments[2]
            3 -> fragments[3]
            else -> throw Exception()
        }
    }
}