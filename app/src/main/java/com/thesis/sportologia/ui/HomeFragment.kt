package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentHomeBinding
import com.thesis.sportologia.ui.views.OnToolbarHomeAction
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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

        return binding.root
    }

    private fun onProfilePicturePressed() {
        findNavController().navigate(R.id.action_homeFragment_to_profile_own,
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