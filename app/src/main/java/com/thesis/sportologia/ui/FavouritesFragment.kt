package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.databinding.FragmentFavouritesBinding
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        binding.toolbar.setListener {
            if (it == OnToolbarBasicAction.LEFT) {
                onBackButtonPressed()
            }
        }

        return binding.root
    }

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }
}