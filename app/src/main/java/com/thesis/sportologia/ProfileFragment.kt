package com.thesis.sportologia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentProfileBinding
import com.thesis.sportologia.views.ContentTabsView
import com.thesis.sportologia.views.SpinnerBasicView
import com.thesis.sportologia.views.SpinnerOnlyOutlinedView

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val contentTabs = binding.root.findViewById<ContentTabsView>(R.id.contentTabs)
        contentTabs.setListener {
            
        }

        contentTabs.setButtonText(1, "Игорь")
        contentTabs.setCount(1, 23)

        val choices = listOf(getString(R.string.posts_all), getString(R.string.posts_upcoming))
        val spinner = binding.root.findViewById<SpinnerOnlyOutlinedView>(R.id.spinner)
        spinner.initAdapter(choices)

        return binding.root
    }
}