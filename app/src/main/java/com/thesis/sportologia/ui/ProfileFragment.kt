package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.entities.Athlete
import com.thesis.sportologia.model.users.entities.Organization
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.posts.ListPostsFragment
import com.thesis.sportologia.ui.posts.ListPostsMode
import com.thesis.sportologia.ui.users.entities.AthleteItem
import com.thesis.sportologia.ui.users.entities.OrganizationItem
import com.thesis.sportologia.ui.users.entities.UserItem
import com.thesis.sportologia.utils.setAvatar
import com.thesis.sportologia.utils.toast
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO проверка на то, userId совпадает с CurrentUser.id
@AndroidEntryPoint
class ProfileFragment  : BaseFragment(R.layout.fragment_profile) {

    @Inject
    lateinit var factory: ProfileViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(getUserIdArg())
    }

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

    private fun getUserIdArg(): String = args.userId

    private fun initRender() {
        binding.toolbar.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fpError.veTryAgain.setOnClickListener {
            viewModel.load()
        }

        val fragments = arrayListOf(
            ListPostsFragment.newInstance(ListPostsMode.PROFILE_OWN_PAGE, getUserIdArg()),
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

    override fun observeViewModel() {
        viewModel.userHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.fpLoading.root.visibility = View.VISIBLE
                    binding.fpError.root.visibility = View.GONE
                    binding.fpContentBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fpLoading.root.visibility = View.GONE
                    binding.fpError.root.visibility = View.GONE
                    binding.fpContentBlock.visibility = View.VISIBLE

                    when (holder.data.lastAction) {
                        UserItem.LastAction.INIT -> {
                            renderUserDetails(holder.data)
                        }
                        UserItem.LastAction.SUBSCRIBE_CHANGED -> {
                            renderUserDetailsOnSubscriptionAction(holder.data)
                        }
                    }
                }
                is DataHolder.ERROR -> {
                    binding.fpLoading.root.visibility = View.GONE
                    binding.fpError.root.visibility = View.VISIBLE
                    binding.fpContentBlock.visibility = View.GONE

                    binding.fpError.veText.text = holder.failure.message
                }
            }
        }

        viewModel.subscribeHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.subscribeButton.isEnabled = false
                }
                is DataHolder.READY -> {
                    binding.subscribeButton.isEnabled = true

                    //renderUserDetailsOnSubscriptionAction(holder.data)
                }
                is DataHolder.ERROR -> {
                    toast(context, getString(R.string.error))
                    binding.subscribeButton.isEnabled = true
                }
            }
        }
    }

    private fun renderUserDetailsOnSubscriptionAction(userItem: UserItem) {
        binding.followersCount.text = userItem.followersCount.toString()
        binding.subscribeButton.setButtonPressed(userItem.isSubscribed)
    }

    private fun renderUserDetails(userItem: UserItem) {
        binding.subscribeButton.setOnClickListener {
            onSubscribeButtonPressed()
        }

        binding.openPhotosButton.setOnClickListener {
            onOpenPhotosButtonPressed()
        }

        binding.followingsLayout.setOnClickListener {
            onOpenFollowingsButton()
        }

        binding.userName.text = userItem.name
        when (userItem) {
            is AthleteItem -> binding.userType.text = getString(R.string.athlete)
            is OrganizationItem -> binding.userType.text = getString(R.string.organization)
        }
        // TODO address formatting
        binding.address.text = userItem.address.toString()
        binding.description.text = userItem.description
        binding.followersCount.text = userItem.followersCount.toString()
        binding.followingsCount.text = userItem.followingsCount.toString()

        setAvatar(userItem.profilePhotoURI, context!!, binding.avatar)

        renderUserDetailsOnSubscriptionAction(userItem)
    }

    private fun onSubscribeButtonPressed() {
        viewModel.onSubscribeButtonPressed()
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