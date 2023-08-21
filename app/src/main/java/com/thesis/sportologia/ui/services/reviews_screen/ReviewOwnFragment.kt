package com.thesis.sportologia.ui.services.reviews_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentReviewOwnBinding
import com.thesis.sportologia.ui.views.SelectStarsView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewOwnFragment : Fragment() {

    private var _binding: FragmentReviewOwnBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewOwnBinding.inflate(inflater, container, false)

        val ratingBlock = binding.root.findViewById<SelectStarsView>(R.id.rating_fro)
        ratingBlock.setListener { }

        return binding.root
    }

    private fun setParams(grade: Int, title: String, text: String) {
        binding.ratingFro.setGrade(grade)
        binding.titleFro.setTitle(title)
        binding.textFro.setText(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}