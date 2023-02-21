package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentFollowingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class FollowingsFragment : Fragment() {
    private lateinit var binding: FragmentFollowingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingsBinding.inflate(inflater, container, false)

        return binding.root
    }
}