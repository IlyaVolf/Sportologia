package com.thesis.sportologia.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        val accountTypes = listOf(getString(R.string.athlete), getString(R.string.organization))
        val spinnerBlockAccountType =
            binding.root.findViewById<SpinnerBasicView>(R.id.spinner_account_type)
        spinnerBlockAccountType.initAdapter(accountTypes, getString(R.string.hint_account_type))

        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val spinnerBlockGender = binding.root.findViewById<SpinnerBasicView>(R.id.spinner_gender)
        spinnerBlockGender.initAdapter(genders, getString(R.string.hint_gender))

        return binding.root
    }


}