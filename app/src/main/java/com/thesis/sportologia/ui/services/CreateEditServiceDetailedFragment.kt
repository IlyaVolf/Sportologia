package com.thesis.sportologia.ui.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentCreateEditServiceDetailedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditServiceDetailedFragment : Fragment() {
    private lateinit var binding: FragmentCreateEditServiceDetailedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditServiceDetailedBinding.inflate(inflater, container, false)

        return binding.root
    }
}