package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListPostsBinding
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.findTopNavController
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ListPostsFragment : Fragment() {

    private lateinit var binding: FragmentListPostsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPostsBinding.inflate(inflater, container, false)

        binding.createPostButton.setOnClickListener {
            onCreatePostButtonPressed()
        }

        return binding.root
    }

    private fun onCreatePostButtonPressed() {
        findTopNavController().navigate(R.id.create_post,
            null,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

}