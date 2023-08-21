package com.thesis.sportologia.ui.signup

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
        observeExceptionMessageEvent()
        observeNavigateToProfileSettingsEvent()

        return binding.root
    }

    private fun initListeners() {
        binding.fsuSignupButton.setOnClickListener {
            val email = binding.fsuEmail.text.toString()
            val password = binding.fsuPassword.text.toString()
            viewModel.onSignUpButtonPressed(email, password)
        }
        binding.fsuBackButton.setOnClickListener {
            goBack()
        }
    }

    private fun observeExceptionMessageEvent() =
        viewModel.toastMessageEvent.observeEvent(viewLifecycleOwner) {
            when (it) {
                SignUpViewModel.ExceptionType.EMAIL_ALREADY_EXISTS -> toast(
                    context,
                    getString(R.string.error_account_with_this_email_exists)
                )
                SignUpViewModel.ExceptionType.EMPTY_PASSWORD -> toast(
                    context,
                    getString(R.string.error_empty_password)
                )
                SignUpViewModel.ExceptionType.EMPTY_EMAIL -> toast(
                    context,
                    getString(R.string.error_empty_email)
                )
                SignUpViewModel.ExceptionType.SHORT_PASSWORD -> toast(
                    context,
                    getString(R.string.error_short_password)
                )
                else -> toast(context, getString(R.string.error))
            }
        }

    private fun observeNavigateToProfileSettingsEvent() =
        viewModel.navigateToProfileSettingsSignUpEvent.observeEvent(viewLifecycleOwner) {
            val direction =
                SignUpFragmentDirections.actionSignUpFragmentToProfileSettingsSignUpFragment(
                    email = binding.fsuEmail.text.toString(),
                    password = binding.fsuPassword.text.toString()
                )

            findNavController().navigate(direction)
        }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun getEmailArgument(): String? = args.email

}