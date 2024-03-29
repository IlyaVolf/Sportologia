package com.thesis.sportologia.ui.services.service_screen

import android.os.Bundle
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
import com.thesis.sportologia.ui.base.PagerAdapter
import com.thesis.sportologia.ui.services.list_services_screen.ListServicesFragmentAcquired
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicesAcquiredFragment : Fragment() {

    private var _binding: FragmentServicesAcquiredBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesAcquiredBinding.inflate(inflater, container, false)

        initOnAuthorPressed()
        initOnInfoPressed()
        initContentBlock()

        return binding.root
    }

    private fun initContentBlock() {
        val listServicesFragmentAcquired =
            ListServicesFragmentAcquired.newInstance(CurrentAccount().id)

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

    private fun initOnInfoPressed() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            GO_TO_SERVICE_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val serviceId = data.getString(SERVICE_ID)
            val direction =
                ServicesAcquiredFragmentDirections.actionServicesAcquiredFragmentToService(
                    serviceId!!
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val GO_TO_OWN_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_OWN_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_PROFILE_REQUEST_CODE = "GO_TO_PROFILE_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_STATS_REQUEST_CODE = "GO_TO_STATS_REQUEST_CODE_FROM_SERV_AQC"
        const val GO_TO_SERVICE_REQUEST_CODE = "GO_TO_SERVICE_REQUEST_CODE_FROM_SERV_AQC"

        const val USER_ID = "USER_ID"
        const val SERVICE_ID = "SERVICE_ID"
    }
}