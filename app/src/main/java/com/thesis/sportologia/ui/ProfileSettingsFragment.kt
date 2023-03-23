package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileSettingsBinding
import com.thesis.sportologia.ui.views.MultiChoiceBasicView
import com.thesis.sportologia.ui.views.SpinnerBasicView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {

    private lateinit var binding: FragmentProfileSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)

        val accountTypes = listOf(getString(R.string.athlete), getString(R.string.organization))
        val spinnerBlockAccountType =
            binding.root.findViewById<SpinnerBasicView>(R.id.account_type)

        //updateLayout(CurrentAccountType.NONE)

        /*when (spinnerBlockAccountType.getCurrentAccountType()) {
            getString(R.string.hint_account_type) -> updateLayout(CurrentAccountType.NONE)
            getString(R.string.athlete) -> updateLayout(CurrentAccountType.ATHLETE)
            getString(R.string.organization) -> updateLayout(CurrentAccountType.ORGANIZATION)
            else -> updateLayout(CurrentAccountType.NONE)
        }*/

        spinnerBlockAccountType.setListener {
            Log.d("BUGFIX", "Listener: $it")
            when (it) {
                getString(R.string.hint_account_type) -> updateLayout(CurrentAccountType.NONE)
                getString(R.string.athlete) -> updateLayout(CurrentAccountType.ATHLETE)
                getString(R.string.organization) -> updateLayout(CurrentAccountType.ORGANIZATION)
                else -> updateLayout(CurrentAccountType.NONE)
            }
        }

        spinnerBlockAccountType.initAdapter(accountTypes)

        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val spinnerBlockGender = binding.root.findViewById<SpinnerBasicView>(R.id.gender)
        spinnerBlockGender.initAdapter(genders)

        val categories = listOf(
            getString(R.string.category_martial_arts),
            getString(R.string.category_aerobics),
            getString(R.string.category_running),
            getString(R.string.category_body_building),
        )
        val categoriesMap = hashMapOf<String, Boolean>()
        categories.forEach{ category -> categoriesMap[category] = false }
        val interests = binding.root.findViewById<MultiChoiceBasicView>(R.id.interests)
        interests.initMultiChoiceList(categoriesMap, getString(R.string.hint_athlete_interests))

        val specializations = binding.root.findViewById<MultiChoiceBasicView>(R.id.specializations)
        specializations.initMultiChoiceList(
            categoriesMap, getString(R.string.hint_organization_specializations)
        )

        return binding.root
    }

    // TODO сброс параметров
    private fun restoreLayout() {
    }

    private fun updateLayout(currentAccountType: CurrentAccountType) {
        with (binding) {
            when (currentAccountType) {
                CurrentAccountType.NONE -> {
                    // accountType.visibility = View.VISIBLE
                    athleteName.visibility = View.GONE
                    organizationName.visibility = View.GONE
                    gender.visibility = View.GONE
                    birthDate.visibility = View.GONE
                    description.visibility = View.GONE
                    interests.visibility = View.GONE
                    specializations.visibility = View.GONE
                    address.visibility = View.GONE
                }
                CurrentAccountType.ATHLETE -> {
                    // accountType.visibility = View.VISIBLE
                    athleteName.visibility = View.VISIBLE
                    organizationName.visibility = View.GONE
                    gender.visibility = View.VISIBLE
                    birthDate.visibility = View.VISIBLE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.VISIBLE
                    specializations.visibility = View.GONE
                    address.visibility = View.VISIBLE
                }
                CurrentAccountType.ORGANIZATION -> {
                    // accountType.visibility = View.VISIBLE
                    athleteName.visibility = View.GONE
                    organizationName.visibility = View.VISIBLE
                    gender.visibility = View.GONE
                    birthDate.visibility = View.GONE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.GONE
                    specializations.visibility = View.VISIBLE
                    address.visibility = View.VISIBLE
                }
            }
        }
    }

    enum class CurrentAccountType {
        NONE,
        ATHLETE,
        ORGANIZATION
    }

}