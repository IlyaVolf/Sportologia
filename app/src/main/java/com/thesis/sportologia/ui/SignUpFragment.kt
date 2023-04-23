package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentSignUpBinding
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val viewModel by viewModels<SignUpViewModel>()

    private val args by navArgs<SignUpFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        if (savedInstanceState == null && getEmailArgument() != null) {
            binding.fsuEmail.setText(getEmailArgument())
        }

        initListeners()
        initObservers()

        return binding.root
    }

    private fun initListeners() {
        binding.fsuSignupButton.setOnClickListener {
            val email = binding.fsuEmail.text.toString()
            val password = binding.fsuPassword.text.toString()
            viewModel.signUp(email, password)
        }
        binding.fsuBackButton.setOnClickListener {
            navigateToBackPage()
        }
    }

    private fun initObservers() = viewModel.state.observe(viewLifecycleOwner) {
        observeShowNicknameErrorMessageEvent()
        observeShowEmailErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    private fun observeShowNicknameErrorMessageEvent() =
        viewModel.showNicknameErrorToastEvent.observeEvent(viewLifecycleOwner) {
            toast(context, getString(R.string.error_account_with_this_nickname_exists))
        }

    private fun observeShowEmailErrorMessageEvent() =
        viewModel.showEmailErrorToastEvent.observeEvent(viewLifecycleOwner) {
            toast(context, getString(R.string.error_account_with_this_email_exists))
        }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }

    private fun navigateToBackPage() {
        findNavController().popBackStack()
    }

    private fun getEmailArgument(): String? = args.email

}