package com.thesis.sportologia.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentSignInBinding
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding
        get() = _binding!!


    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        initListeners()
        initObservers()

        return binding.root
    }

    private fun initListeners() {
        binding.fsiSigninButton.setOnClickListener {
            val email = binding.fsiEmail.text.toString()
            val password = binding.fsiPassword.text.toString()
            viewModel.signIn(email, password)
        }
        binding.fsiSignupButton.setOnClickListener {
            navigateToSignUpPage()
        }
    }

    private fun initObservers() = viewModel.state.observe(viewLifecycleOwner) {
        observeShowAuthErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    private fun observeShowAuthErrorMessageEvent() =
        viewModel.showAuthErrorToastEvent.observeEvent(viewLifecycleOwner) {
            toast(context, getString(R.string.error_invalid_email_or_password))
        }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }

    private fun navigateToSignUpPage() {
        val email = binding.fsiEmail.text.toString()
        val emailArg = email.ifBlank { null }

        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment(emailArg)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}