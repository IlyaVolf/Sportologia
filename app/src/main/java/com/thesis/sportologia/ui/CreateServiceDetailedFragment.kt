package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentCreateServiceDetailedBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class CreateServiceDetailedFragment : Fragment() {
    private lateinit var binding: FragmentCreateServiceDetailedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateServiceDetailedBinding.inflate(inflater, container, false)

        return binding.root
    }
}