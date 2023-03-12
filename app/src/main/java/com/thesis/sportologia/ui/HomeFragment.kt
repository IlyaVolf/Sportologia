package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentHomeBinding
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.posts.ListPostsFragment
import com.thesis.sportologia.ui.posts.ListPostsMode
import com.thesis.sportologia.ui.views.OnToolbarHomeAction
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    // TODO нормальная адаптивная высота viewpager'а!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.toolbar.setListener {
            when (it) {
                OnToolbarHomeAction.LEFT -> onProfilePicturePressed()
                OnToolbarHomeAction.RIGHT -> onSettingsPressed()
            }
        }

        val fragments = arrayListOf(
            ListPostsFragment.newInstance(ListPostsMode.HOME_PAGE, CurrentAccount().id),
            ListEventsFragment()
        )
        adapter = PagerAdapter(this, fragments)
        viewPager = binding.pager
        viewPager.adapter = adapter

        /*viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private val listener = ViewTreeObserver.OnGlobalLayoutListener {
                val view = fragments[0].view // ... get the view
                updatePagerHeightForChild(view!!)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val view = fragments[0].view// ... get the view
                // ... IMPORTANT: remove the global layout listener from other views
                fragments.forEach {
                    it.view!!.viewTreeObserver.removeOnGlobalLayoutListener(
                        layoutListener
                    )
                }
                view!!.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
            }

            private fun updatePagerHeightForChild(view: View) {
                view.post {
                    val wMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
                    val hMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    view.measure(wMeasureSpec, hMeasureSpec)

                    if (viewPager.layoutParams.height != view.measuredHeight) {
                        // ParentViewGroup is, for example, LinearLayout
                        // ... or whatever the parent of the ViewPager2 is
                        viewPager.layoutParams =
                            (viewPager.layoutParams as ParentViewGroup.LayoutParams)
                                .also { lp -> lp.height = view.measuredHeight }
                    }
                }
            }
        })*/

        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.posts)
                1 -> getString(R.string.events)
                else -> ""
            }
        }.attach()

        return binding.root
    }

    private fun onProfilePicturePressed() {
        findNavController().navigate(R.id.action_homeFragment_to_profile,
            null,
            navOptions {
                /* чтобы заменить фрагмент главного экрана на фрагмент страницы пользователя,
                * а не положить оно поверх другого */
                popUpTo(R.id.homeFragment) {
                    inclusive = true
                }
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            })
    }

    private fun onSettingsPressed() {
        findTopNavController().navigate(R.id.settingsFragment,
            null,
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