package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.databinding.FragmentPhotosBinding
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class PhotosFragment : Fragment() {
    private lateinit var binding: FragmentPhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)

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