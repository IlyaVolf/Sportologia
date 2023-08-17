package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.thesis.sportologia.databinding.FragmentPhotosBinding
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.photos.ListPhotosFragment
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotosFragment : Fragment() {
    private val args by navArgs<PhotosFragmentArgs>()

    private lateinit var binding: FragmentPhotosBinding
    private lateinit var userId: String
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2

    private fun getUserId(): String = args.userId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)
        userId = getUserId()

        initToolbar()
        initContentBlock()

        return binding.root
    }

    private fun initToolbar() {
        binding.photosToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onBackButtonPressed()
                else -> {}
            }
        }
    }

    private fun initContentBlock() {
        val listPhotosFragment = ListPhotosFragment.newInstance(userId)

        adapter = PagerAdapter(this, arrayListOf(listPhotosFragment))
        viewPager = binding.pager
        viewPager.adapter = adapter
    }

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

}