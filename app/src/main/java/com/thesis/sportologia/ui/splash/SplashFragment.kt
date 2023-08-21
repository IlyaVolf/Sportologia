package com.thesis.sportologia.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentSplashBinding
import com.thesis.sportologia.ui.MainActivity
import com.thesis.sportologia.ui.MainActivityArgs
import com.thesis.sportologia.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding

    private val viewModel by viewModels<SplashViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSplashBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        //renderAnimations()

        viewModel.launchMainScreenEvent.observeEvent(viewLifecycleOwner) { launchMainScreen(it) }
    }

    private fun launchMainScreen(isSignedIn: Boolean) {
        val intent = Intent(requireContext(), MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val args = MainActivityArgs(isSignedIn)
        intent.putExtras(args.toBundle())
        startActivity(intent)
    }

}
