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
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileOwnBinding
import com.thesis.sportologia.ui.apdaters.HomePagerAdapter
import com.thesis.sportologia.ui.views.*
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ProfileOwnFragment : Fragment() {
    private lateinit var binding: FragmentProfileOwnBinding

    private lateinit var adapter: HomePagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileOwnBinding.inflate(inflater, container, false)

        val profileSettingsButton = binding.profileSettingsButton.setOnClickListener {
            onProfileSettingsButtonPressed()
        }

        val favouritesButton = binding.favouritesButton.setOnClickListener {
            onFavouritesButtonPressed()
        }

        val openPhotosButton = binding.openPhotosButton.setOnClickListener {
            onOpenPhotosButtonPressed()
        }

        /*val createPostButton = binding.createPostButton.setOnClickListener{
            onCreatePostButtonPressed()
        }*/

        /* val contentTabs = binding.root.findViewById<ContentTabsView>(R.id.contentTabs)
        contentTabs.setListener {

        }

        contentTabs.setButtonText(1, "Игорь")
        contentTabs.setCount(1, 23)*/

        /*val post = binding.root.findViewById<ItemPostView>(R.id.item_post)
        post.setListener { }
        post.setUsername("Игорь Чиёсов")
        post.setText("Привет")
        post.setLikes(311331, true)
        post.setFavs(true)
        post.setAvatar(URI("https://i.imgur.com/tGbaZCY.jpg"))


        val review = binding.root.findViewById<ItemReviewView>(R.id.item_review)
        review.setListener { }
        review.setUsername("Игорь Чиёсов")
        review.setTitle("Много воды")
        review.setDescription("Я Игорь, я люблю Андрея")
        review.setRating(2)
        review.setAvatar(URI("https://i.imgur.com/tGbaZCY.jpg"))

        val event = binding.root.findViewById<ItemEventView>(R.id.item_event)
        event.setListener { }
        event.setLikes(25363636, true)
        event.setFavs(false)
        event.setOrganizerName("Игорь Чиёсов")
        event.setDescription(getString(R.string.test_text))
        event.setPrice("0", getString(R.string.ruble_abbreviation))
        event.setAvatar(URI("https://i.imgur.com/tGbaZCY.jpg"))

        val service = binding.root.findViewById<ItemServiceView>(R.id.item_service)
        service.setListener { }
        service.setFavs(false)
        service.setOrganizerName("Игорь Чиёсов")
        service.setDescription(getString(R.string.test_text))
        service.setPrice("4224", getString(R.string.ruble_abbreviation))
        service.setAvatar(URI("https://i.imgur.com/tGbaZCY.jpg"))*/


        ////////////////////////////

        adapter = HomePagerAdapter(this)
        viewPager = binding.pager
        viewPager.adapter = adapter


        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.posts) // + "amount"
                1 -> getString(R.string.services)
                2 -> getString(R.string.events)
                else -> ""
            }
        }.attach()

        ///////////////////////////

        return binding.root
    }

    private fun onProfileSettingsButtonPressed() {
        findTopNavController().navigate(R.id.profileSettingsFragment,
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

    private fun onFavouritesButtonPressed() {
        findNavController().navigate(R.id.action_profileOwnFragment_to_favouritesFragment,
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
}