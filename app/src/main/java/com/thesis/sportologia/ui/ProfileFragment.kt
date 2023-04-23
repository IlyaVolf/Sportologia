package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.ui.posts.ListPostsFragmentProfileOther
import com.thesis.sportologia.ui.posts.ListPostsFragmentProfileOwn
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.events.ListEventsFragmentProfileOther
import com.thesis.sportologia.ui.events.ListEventsFragmentProfileOwn
import com.thesis.sportologia.ui.services.ListServicesFragmentProfileOther
import com.thesis.sportologia.ui.services.ListServicesFragmentProfileOwn
import com.thesis.sportologia.ui.users.entities.AthleteListItem
import com.thesis.sportologia.ui.users.entities.OrganizationListItem
import com.thesis.sportologia.ui.users.entities.UserListItem
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODO сохранять состояния экрана при навигации. Ибо при навигации создаётся новый экземпляр и загрузка повторяется вновь и вновь
// TODO баг при мене темы - кнопка subscribe не работает!
@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    @Inject
    lateinit var factory: ProfileViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(getUserIdArg())
    }

    private lateinit var mode: Profile
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userId: String
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val args by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        userId = getUserIdArg()

        initMod()
        when (mode) {
            Profile.OWN -> initRenderProfileOwn()
            Profile.OTHER -> initRenderProfileOther()
        }

        initNavToProfile()

        return binding.root
    }

    private fun initMod() {
        mode = if (userId == CurrentAccount().id) {
            Profile.OWN
        } else {
            Profile.OTHER
        }
    }

    private fun getUserIdArg(): String {
        val userId = args.userId
        return if (userId == DEFAULT_USER_ID) {
            CurrentAccount().id
        } else {
            userId
        }
    }

    private fun initRenderProfileOwn() {
        binding.toolbar.root.visibility = View.GONE
        binding.subscribeButton.visibility = View.GONE
        binding.settingsFavsButtonsBlock.visibility = View.VISIBLE
        binding.subscribeButtonsBlock.visibility = View.GONE

        initProfileSettingsButton()
        initFavouritesButton()
        initFollowersButton()
        initFollowingsButton()
        initRefreshLayout()
        initContentBlock()
        initErrorProcessing()
    }

    private fun initRenderProfileOther() {
        binding.toolbar.root.visibility = View.VISIBLE
        binding.subscribeButton.visibility = View.VISIBLE
        binding.settingsFavsButtonsBlock.visibility = View.GONE
        binding.subscribeButtonsBlock.visibility = View.VISIBLE

        initToolbar()
        initOnInfoPressed()
        initFollowersButton()
        initFollowingsButton()
        initRefreshLayout()
        initContentBlock()
        initErrorProcessing()
    }

    private fun initErrorProcessing() {
        binding.fpError.veTryAgain.setOnClickListener {
            viewModel.init()
            refreshContentBlock()
        }
    }

    private fun initToolbar() {
        binding.toolbar.title.text = "@$userId"

        binding.toolbar.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initProfileSettingsButton() {
        binding.profileSettingsButton.setOnClickListener {
            onProfileSettingsButtonPressed()
        }
    }

    private fun initFollowersButton() {
        binding.followersLayout.setOnClickListener {
            onFollowersButtonPressed()
        }
    }

    private fun initFollowingsButton() {
        binding.followingsLayout.setOnClickListener {
            onFollowingsButtonPressed()
        }
    }

    private fun initFavouritesButton() {
        binding.favouritesButton.setOnClickListener {
            onFavouritesButtonPressed()
        }
    }

    private fun initRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()

            requireActivity().supportFragmentManager.setFragmentResult(
                REFRESH_POSTS_LIST_KEY,
                bundleOf()
            )

            requireActivity().supportFragmentManager.setFragmentResult(
                REFRESH_SERVICES_LIST_KEY,
                bundleOf()
            )

            requireActivity().supportFragmentManager.setFragmentResult(
                REFRESH_EVENTS_LIST_KEY,
                bundleOf()
            )
        }

        refreshContentBlock()
    }

    private fun refreshContentBlock() {
        binding.materialupAppbar.addOnOffsetChangedListener(
            OnOffsetChangedListener { _, verticalOffset ->
                binding.swipeRefreshLayout.isEnabled = verticalOffset == 0
            })
    }

    private fun initContentBlock() {
        /*val listPostsFragment = when (mode) {
            Profile.OWN -> ListPostsFragmentProfileOwn.newInstance(userId)
            Profile.OTHER -> ListPostsFragmentProfileOther.newInstance(userId)
        }
        val listEventsFragment = when (mode) {
            Profile.OWN -> ListEventsFragmentProfileOwn.newInstance(userId)
            Profile.OTHER -> ListEventsFragmentProfileOther.newInstance(userId)
        }
        val fragments = arrayListOf(
            listPostsFragment,
            ListServicesFragment(),
            listEventsFragment
        )
        adapter = PagerAdapter(this, fragments)

        tabLayout = binding.tabLayout

        childFragmentManager
            .beginTransaction()
            //.addToBackStack(null)
            .replace(R.id.fragmentContainer, fragments[0])
            .commit()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                tab.text = when (tab.position) {
                    0 -> getString(R.string.posts) // + "amount"
                    1 -> getString(R.string.services)
                    2 -> getString(R.string.events)
                    else -> ""
                }

                childFragmentManager
                    .beginTransaction()
                    //.addToBackStack(null)
                    .replace(R.id.fragmentContainer, fragments[tab.position])
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })*/

        val listPostsFragment = when (mode) {
            Profile.OWN -> ListPostsFragmentProfileOwn.newInstance(userId)
            Profile.OTHER -> ListPostsFragmentProfileOther.newInstance(userId)
        }
        val listServicesFragment = when (mode) {
            Profile.OWN -> ListServicesFragmentProfileOwn.newInstance(userId)
            Profile.OTHER -> ListServicesFragmentProfileOther.newInstance(userId)
        }
        val listEventsFragment = when (mode) {
            Profile.OWN -> ListEventsFragmentProfileOwn.newInstance(userId)
            Profile.OTHER -> ListEventsFragmentProfileOther.newInstance(userId)
        }
        val fragments = arrayListOf(
            listPostsFragment,
            listServicesFragment,
            listEventsFragment
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
                DataHolder.INIT -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.fpLoading.root.visibility = View.VISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.INVISIBLE
                }
                DataHolder.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                    binding.fpLoading.root.visibility = View.INVISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.VISIBLE
                }
                is DataHolder.READY -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.fpLoading.root.visibility = View.INVISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.VISIBLE

                    renderUserDetails(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.fpLoading.root.visibility = View.INVISIBLE
                    binding.fpError.root.visibility = View.VISIBLE
                    binding.fpContentBlock.visibility = View.INVISIBLE

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
                }
                is DataHolder.ERROR -> {
                    toast(context, getString(R.string.error))
                    binding.subscribeButton.isEnabled = true
                }
            }
        }
    }

    private fun renderUserDetailsOnSubscriptionAction(userItem: UserListItem) {
        binding.followersCount.text = userItem.followersCount.toString()
        binding.subscribeButton.setButtonPressed(userItem.isSubscribed)
    }

    private fun renderPhotosBlock(photosCount: Int, photosSnippets: List<String>) {
        binding.photosBlock.photoLabelAndCount.text =
            getString(R.string.photos) + " (" + photosCount + ")"
        binding.photosBlock.photosRow.setPhotos(photosSnippets)
    }

    private fun renderUserDetails(userItem: UserListItem) {
        binding.subscribeButton.setOnClickListener {
            onSubscribeButtonPressed()
        }

        binding.photosBlock.root.setOnClickListener {
            onOpenPhotosButtonPressed()
        }

        binding.userName.text = userItem.name
        when (userItem) {
            is AthleteListItem -> binding.userType.text = getString(R.string.athlete)
            is OrganizationListItem -> binding.userType.text = getString(R.string.organization)
        }
        binding.description.text = userItem.description
        binding.followersCount.text = userItem.followersCount.toString()
        binding.followingsCount.text = userItem.followingsCount.toString()
        binding.categories.text = getCategoriesText(userItem)

        val addressText = YandexMaps.getAddress(context!!, userItem.position)
        binding.address.text = addressText ?: getString(R.string.not_specified)

        setAvatar(userItem.profilePhotoURI, context!!, binding.avatar)

        renderUserDetailsOnSubscriptionAction(userItem)
        renderPhotosBlock(userItem.photosCount, userItem.photosSnippets)
    }

    private fun getCategoriesText(userItem: UserListItem): String {
        val categoriesLocalized = hashMapOf<String, Boolean>()
        userItem.categories.forEach {
            categoriesLocalized.put(
                Categories.convertEnumToCategory(
                    context,
                    it.key
                )!!,
                it.value
            )
        }

        val concatCategories = concatMap(categoriesLocalized, ", ")

        return if (concatCategories == "") {
            getString(R.string.categories_not_specified)
        } else {
            concatCategories
        }
    }

    // навигация к другому пользователю
    private fun initNavToProfile() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo = data.getString(USER_ID) ?: return@setFragmentResultListener
            if (userIdToGo != userId) {
                val direction =
                    ProfileFragmentDirections.actionProfileFragmentToProfileFragment(userIdToGo)
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
    }

    private fun onSubscribeButtonPressed() {
        viewModel.onSubscribeButtonPressed()
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
            R.id.action_profileFragment_to_favouritesFragment,
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
        val direction =
            ProfileFragmentDirections.actionProfileFragmentToPhotosFragment(userId)
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

    private fun onFollowersButtonPressed() {
        val direction =
            ProfileFragmentDirections.actionProfileFragmentToFollowersFragment(userId)
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

    private fun onFollowingsButtonPressed() {
        val direction =
            ProfileFragmentDirections.actionProfileFragmentToFollowingsFragment(userId)
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

    private fun initOnInfoPressed() {
        /*requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_SERVICE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val serviceId = data.getLong(SERVICE_ID)
            val direction =
                ProfileFragmentDirections.actionProfileFragmentToService(
                    serviceId
                )
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
        }*/
    }

    companion object {
        const val DEFAULT_USER_ID = "\$current_user"

        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_PROFILE"
        const val GO_TO_STATS_REQUEST_CODE = "GO_TO_STATS_REQUEST_CODE_FROM_PROFILE"
        const val GO_TO_SERVICE_REQUEST_CODE = "GO_TO_SERVICE_REQUEST_CODE_FROM_PROFILE"

        const val USER_ID = "USER_ID"
        const val SERVICE_ID = "SERVICE_ID"

        const val ABCDEFG = "ABCDEFG"
        const val REFRESH = "REFRESH"
    }

    enum class Profile {
        OWN,
        OTHER
    }
}