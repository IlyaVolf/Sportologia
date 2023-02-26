package com.thesis.sportologia.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.thesis.sportologia.ui.ListEventsFragment
import com.thesis.sportologia.ui.ListPostsFragment
import com.thesis.sportologia.ui.ListServicesFragment


class HomePagerAdapter2(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            //0 -> ListPostsFragment()
            1 -> ListServicesFragment()
            2 -> ListEventsFragment()
            else -> throw Exception()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Tab 1"
            }
            1 -> {
                return "Tab 2"
            }
            2 -> {
                return "Tab 3"
            }
        }
        return super.getPageTitle(position)
    }

}