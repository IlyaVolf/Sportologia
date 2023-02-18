package com.thesis.sportologia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentFilterBinding
import com.thesis.sportologia.views.*
import java.net.URI

class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        return binding.root
    }
}