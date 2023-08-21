package com.thesis.sportologia.ui.search.filter_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentFilterBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

/**@AndroidEntryPoint
abstract class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.fragmentFilterApply.setOnClickListener {
            onApplyButtonPressed()
        }
        binding.fragmentFilterCancel.setOnClickListener {
            onCancelButtonPressed()
        }

        return binding.root
    }

    private fun onApplyButtonPressed() {
        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf()
        )
    }

    private fun onCancelButtonPressed() {
        requireActivity().supportFragmentManager.setFragmentResult(
            SearchFragment.FILTER_REQUEST_CODE,
            bundleOf()
        )
    }

}*/