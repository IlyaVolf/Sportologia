package com.thesis.sportologia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentServiceBinding
import com.thesis.sportologia.views.*
import java.net.URI

class ServiceFragment : Fragment() {
    private lateinit var binding: FragmentServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)

        val searchBar = binding.root.findViewById<SearchView>(R.id.search_bar)
        searchBar.setOnSearchClickListener{}


        return binding.root
    }
}