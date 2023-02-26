package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentCreateExerciseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateExerciseFragment : Fragment() {
    private lateinit var binding: FragmentCreateExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateExerciseBinding.inflate(inflater, container, false)

        return binding.root
    }
}