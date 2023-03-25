package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentServicesAcquiredBinding
import com.thesis.sportologia.ui.adapters.PagerAdapter
import com.thesis.sportologia.ui.services.ListServicesFragmentAcquired
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicesAcquiredFragment : Fragment() {

    private lateinit var binding: FragmentServicesAcquiredBinding
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesAcquiredBinding.inflate(inflater, container, false)

        initOnAuthorPressed()
        initContentBlock()

        return binding.root
    }

    private fun initContentBlock() {
        val listServicesFragmentAcquired =  ListServicesFragmentAcquired.newInstance(CurrentAccount().id)

        adapter = PagerAdapter(this, arrayListOf(listServicesFragmentAcquired))
        viewPager = binding.pager
        viewPager.adapter = adapter
    }

    private fun initOnAuthorPressed() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_PROFILE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val userIdToGo = data.getString(USER_ID) ?: return@setFragmentResultListener
            if (userIdToGo != CurrentAccount().id) {
                val direction =
                    ServicesAcquiredFragmentDirections.actionServicesAcquiredFragmentToProfileNested(
                        userIdToGo
                    )
                findNavController().navigate(
                    direction,
                    navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    })
            } else {
                requireActivity().supportFragmentManager.setFragmentResult(
                    GO_TO_OWN_PROFILE_REQUEST_CODE,
                    bundleOf()
                )
            }
        }
    }

    companion object {
        const val GO_TO_OWN_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_OWN_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_STATS_REQUEST_CODE = "GO_TO_STATS_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_SERVICE_REQUEST_CODE = "GO_TO_SERVICE_REQUEST_CODE_FROM_SERV_AQC"

        const val USER_ID = "USER_ID"
    }
}