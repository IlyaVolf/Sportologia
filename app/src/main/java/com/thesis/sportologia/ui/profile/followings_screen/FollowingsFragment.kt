package com.thesis.sportologia.ui.profile.followings_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentFollowingsBinding
import com.thesis.sportologia.ui.base.PagerAdapter
import com.thesis.sportologia.ui.users.list_users_screen.ListUsersFragmentFollowings
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowingsFragment : Fragment() {
    private val args by navArgs<FollowingsFragmentArgs>()

    private lateinit var binding: FragmentFollowingsBinding
    private lateinit var userId: String
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2

    private fun getUserId(): String = args.userId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingsBinding.inflate(inflater, container, false)
        userId = getUserId()

        initToolbar()
        initOnFollowerPressed()
        initContentBlock()

        return binding.root
    }

    private fun initToolbar() {
        binding.followingsToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onBackButtonPressed()
                else -> {}
            }
        }
    }

    private fun initOnFollowerPressed() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo = data.getString(USER_ID) ?: return@setFragmentResultListener
            if (userIdToGo != userId) {
                val direction =
                    FollowingsFragmentDirections.actionFollowingsFragmentToProfileFragment(
                        userIdToGo
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
            }
        }
    }

    private fun initContentBlock() {
        val listUsersFragmentFollowings =  ListUsersFragmentFollowings.newInstance(userId)

        adapter = PagerAdapter(this, arrayListOf(listUsersFragmentFollowings))
        viewPager = binding.pager
        viewPager.adapter = adapter
    }

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

    companion object {
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_FOLLOWERS"
        const val USER_ID = "USER_ID"
    }
}