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
import com.thesis.sportologia.databinding.FragmentListEventsBinding
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ListEventsFragment : Fragment() {

    private lateinit var binding: FragmentListEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListEventsBinding.inflate(inflater, container, false)

        return binding.root
    }

}