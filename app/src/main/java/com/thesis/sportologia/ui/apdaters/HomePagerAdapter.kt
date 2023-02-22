package com.thesis.sportologia.ui.apdaters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.thesis.sportologia.R
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