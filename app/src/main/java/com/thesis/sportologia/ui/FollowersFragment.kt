package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentFollowersBinding
import com.thesis.sportologia.ui.users.ListUsersFragmentFollowers
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersFragment : Fragment() {
    private val args by navArgs<FollowersFragmentArgs>()

    private lateinit var binding: FragmentFollowersBinding
    private lateinit var userId: String
    private lateinit var listUsersFragmentFollowers: ListUsersFragmentFollowers

    private fun getUserId(): String = args.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = getUserId()
        listUsersFragmentFollowers = ListUsersFragmentFollowers.newInstance(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowersBinding.inflate(inflater, container, false)

        initToolbar()
        initOnFollowerPressed()
        initListFragment()

        return binding.root
    }

    private fun initToolbar() {
        binding.followersToolbar.setListener {
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
                    FollowersFragmentDirections.actionFollowersFragmentToProfileFragment(userIdToGo)
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

    override fun onDestroyView() {
        super.onDestroyView()

        requireActivity().supportFragmentManager.beginTransaction()
            .remove(listUsersFragmentFollowers).commit()
    }

    private fun initListFragment() {
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.followings_list_container, listUsersFragmentFollowers
        ).commit()
    }

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

    companion object {
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_FOLLOWERS"
        const val USER_ID = "USER_ID"
    }
}