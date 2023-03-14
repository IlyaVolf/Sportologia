package com.thesis.sportologia.ui._obsolete

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileOwnFragment : Fragment() {
    /**private lateinit var binding: FragmentProfileOwnBinding

    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileOwnBinding.inflate(inflater, container, false)

        initRender()
        /*

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

        return binding.root
    }

    private fun initRender() {
        binding.profileSettingsButton.setOnClickListener {
            onProfileSettingsButtonPressed()
        }

        binding.favouritesButton.setOnClickListener {
            onFavouritesButtonPressed()
        }

        binding.openPhotosButton.setOnClickListener {
            onOpenPhotosButtonPressed()
        }

        binding.followingsLayout.setOnClickListener {
            onOpenFollowingsButton()
        }

        val fragments = arrayListOf(
            ListPostsFragment.newInstance(ListPostsMode.PROFILE_OWN_PAGE, CurrentAccount().id),
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
        findNavController().navigate(
            R.id.action_profileOwnFragment_to_favouritesFragment,
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
    }*/
}