package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileBinding
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.views.*
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val args by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initRender()

        return binding.root
    }

    //private fun getUserIdArg(): Long = args.userId

    private fun initRender() {
        binding.toolbar.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.subscribeButton.setOnClickListener {
        }

        binding.openPhotosButton.setOnClickListener {
            onOpenPhotosButtonPressed()
        }

        binding.followingsLayout.setOnClickListener {
            onOpenFollowingsButton()
        }

        val fragments = arrayListOf(
            ListPostsFragment.newInstance(ListPostsMode.PROFILE_OWN_PAGE),
            ListServicesFragment(),
            ListEventsFragment()
        )
        adapter = PagerAdapter(this, fragments)
        viewPager = binding.pager
        viewPager.adapter = adapter

        tabLayout = binding.tabLayout

        // TODO указать кол-во постов и т.п. через создание самой vm и соответ методов
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.posts) // + "amount"
                1 -> getString(R.string.services)
                2 -> getString(R.string.events)
                else -> ""
            }
        }.attach()
    }

    private fun onOpenPhotosButtonPressed() {
        findNavController().navigate(R.id.action_profileOwnFragment_to_photosFragment,
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

    private fun onOpenFollowingsButton() {
        findNavController().navigate(R.id.action_profileOwnFragment_to_listPostsFragment,
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