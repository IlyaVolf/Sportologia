package com.thesis.sportologia.ui.search.search_screen

/**@AndroidEntryPoint
class SearchContainerFragment : BaseFragment(R.layout.fragment_home) {

    override val viewModel by viewModels<HomeViewModel>()

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

        initToolbar()
        initContentBlock()
        initNavToProfile()

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.setListener {
            when (it) {
                OnToolbarHomeAction.LEFT -> onProfilePicturePressed()
                OnToolbarHomeAction.RIGHT -> onSettingsPressed()
            }
        }
    }

    private fun initContentBlock() {
        val fragments = arrayListOf(
            ListPostsFragmentHome.newInstance(CurrentAccount().id),
            ListEventsFragmentHome.newInstance(CurrentAccount().id),
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
    }

    // навигация к другому пользователю
    private fun initNavToProfile() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo = data.getString(USER_ID) ?: return@setFragmentResultListener
            val direction =
                HomeFragmentDirections.actionHomeFragmentToProfileNested(userIdToGo)
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

    override fun observeViewModel() {
        viewModel.avatarHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {}
                DataHolder.INIT -> {}
                is DataHolder.READY -> {
                    setAvatar(holder.data, context!!, binding.toolbar.avatar)
                }
                is DataHolder.ERROR -> {}
            }
        }
    }

    // TODO очищать стек вкладки profile_own
    private fun onProfilePicturePressed() {
        requireActivity().supportFragmentManager.setFragmentResult(
            REQUEST_CODE,
            bundleOf()
        )

        /*findNavController().navigate(R.id.action_homeFragment_to_profile_own,
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
            })*/
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

    companion object {
        const val REQUEST_CODE = "GO_TO_PROFILE_OWN_REQUEST_CODE"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_HOME"
        const val USER_ID = "USER_ID"
    }
}*/