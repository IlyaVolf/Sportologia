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
import com.thesis.sportologia.ui.posts.ListPostsFragmentProfileOther
import com.thesis.sportologia.ui.posts.ListPostsFragmentProfileOwn
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.events.ListEventsFragment
import com.thesis.sportologia.ui.events.ListEventsFragmentProfileOther
import com.thesis.sportologia.ui.events.ListEventsFragmentProfileOwn
import com.thesis.sportologia.ui.users.entities.AthleteItem
import com.thesis.sportologia.ui.users.entities.OrganizationItem
import com.thesis.sportologia.ui.users.entities.UserItem
import com.thesis.sportologia.utils.findTopNavController
import com.thesis.sportologia.utils.setAvatar
import com.thesis.sportologia.utils.toast
import com.thesis.sportologia.utils.viewModelCreator
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
        Log.d("BUGFIX", "${this.hashCode()}")

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        userId = getUserIdArg()
        mode = if (userId == CurrentAccount().id) {
            Profile.OWN
        } else {
            Profile.OTHER
        }
        when (mode) {
            Profile.OWN -> initRenderProfileOwn()
            Profile.OTHER -> initRenderProfileOther()
        }

        initNavToProfile()

        return binding.root
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

    private fun initFavouritesButton() {
        binding.favouritesButton.setOnClickListener {
            onFavouritesButtonPressed()
        }
    }

    private fun initRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()

            requireActivity().supportFragmentManager.setFragmentResult(
                REFRESH_REQUEST_CODE,
                bundleOf(REFRESH to true)
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
        val listPostsFragment = when (mode) {
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
                    binding.swipeRefreshLayout.isRefreshing = true
                    binding.fpLoading.root.visibility = View.INVISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.VISIBLE
                }
                DataHolder.INIT -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.fpLoading.root.visibility = View.VISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.INVISIBLE
                }
                is DataHolder.READY -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.fpLoading.root.visibility = View.INVISIBLE
                    binding.fpError.root.visibility = View.INVISIBLE
                    binding.fpContentBlock.visibility = View.VISIBLE

                    renderUserDetails(holder.data)
                    // TODO идея, чтобы при подписке все не перезагружать. Но работает так себе
                    // TODO при переходе от одного экрана  к другому, видимо, все заново грузит
                    /*    when (holder.data.lastAction) {
                            UserItem.LastAction.INIT -> {
                                renderUserDetails(holder.data)
                            }
                            UserItem.LastAction.SUBSCRIBE_CHANGED -> {
                                renderUserDetailsOnSubscriptionAction(holder.data)
                            }
                     }*/
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

    private fun renderUserDetailsOnSubscriptionAction(userItem: UserItem) {
        binding.followersCount.text = userItem.followersCount.toString()
        binding.subscribeButton.setButtonPressed(userItem.isSubscribed)
    }

    private fun renderUserDetails(userItem: UserItem) {
        Log.d("BUGFIX", "1313131313")

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
        binding.categories.text = getCategoriesText(userItem)

        setAvatar(userItem.profilePhotoURI, context!!, binding.avatar)

        renderUserDetailsOnSubscriptionAction(userItem)
    }

    private fun getCategoriesText(userItem: UserItem): String {
        val res = StringBuilder()

        var flag = false
        for (category in userItem.categories) {
            if (flag) {
                res.append(", ")
                flag = false
            }
            if (category.value) {
                res.append(category.key)
                flag = true
            }
        }

        return if (res.toString() == "") {
            getString(R.string.categories_not_specified)
        } else {
            res.toString()
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
        findNavController().navigate(R.id.action_profileFragment_to_photosFragment,
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
        /*findNavController().navigate(R.id.action_profileFragment_to_listPostsFragment,
            null,
            navOptions {
                anim {
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_right
                }
            })*/
    }

    companion object {
        const val DEFAULT_USER_ID = "\$current_user"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_PROFILE"
        const val REFRESH_REQUEST_CODE = "REFRESH_REQUEST_CODE"
        const val REFRESH = "REFRESH"
        const val USER_ID = "USER_ID"
    }

    enum class Profile {
        OWN,
        OTHER
    }
}