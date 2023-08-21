package com.thesis.sportologia.ui.signup

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileSettingsSignupBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.entities.GenderType
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.ui.entities.ProfileSettingsViewItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsSignUpFragment : Fragment() {

    private lateinit var binding: FragmentProfileSettingsSignupBinding

    private var currentAccountType = CurrentAccountType.NONE
    private val viewModel by viewModels<ProfileSettingsSignUpViewModel>()
    private var profilePhoto: String? = null

    private lateinit var email: String
    private lateinit var password: String

    private val args by navArgs<ProfileSettingsSignUpFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsSignupBinding.inflate(inflater, container, false)

        email = getEmailArgument()
        password = getPasswordArgument()

        updateLayout()

        initToolbar()
        initProfilePhotoButton()
        initAccountTypeSpinner()
        initGenderSpinner()
        initInterestsMultiChoiceList()
        initSpecializationsMultiChoiceList()

        observeViewModel()

        return binding.root
    }

    private fun initToolbar() {
        binding.fpssToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> {
                    goBack()
                }
                OnToolbarBasicAction.RIGHT -> {
                    onSaveButtonPressed()
                }
            }
        }
    }

    private fun initProfilePhotoButton() {
        binding.avatar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
    }

    private fun initAccountTypeSpinner() {
        binding.accountType.setListener {
            currentAccountType = when (it) {
                getString(R.string.athlete) -> {
                    CurrentAccountType.ATHLETE
                }
                getString(R.string.organization) -> {
                    CurrentAccountType.ORGANIZATION
                }
                else -> {
                    CurrentAccountType.NONE
                }
            }

            updateLayout()
        }

        val accountTypes = listOf(getString(R.string.athlete), getString(R.string.organization))
        binding.accountType.initAdapter(accountTypes)
    }

    private fun initGenderSpinner() {
        val genders = listOf(getString(R.string.male), getString(R.string.female))
        binding.gender.initAdapter(genders)
    }

    private fun initInterestsMultiChoiceList() {
        val categoriesMap = Categories.emptyCategoriesMap
        val categoriesLocalizedMap = Categories.getLocalizedCategories(context!!, categoriesMap)
        binding.interests.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.hint_athlete_interests)
        )
    }

    private fun initSpecializationsMultiChoiceList() {
        val categoriesMap = Categories.emptyCategoriesMap
        val categoriesLocalizedMap = Categories.getLocalizedCategories(context!!, categoriesMap)
        binding.specializations.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.hint_organization_specializations)
        )
    }

    private fun updateLayout() {
        with(binding) {
            when (currentAccountType) {
                CurrentAccountType.NONE -> {
                    accountType.visibility = View.VISIBLE
                    athleteFirstname.visibility = View.GONE
                    athleteLastname.visibility = View.GONE
                    organizationName.visibility = View.GONE
                    nickname.visibility = View.GONE
                    gender.visibility = View.GONE
                    birthDate.visibility = View.GONE
                    description.visibility = View.GONE
                    interests.visibility = View.GONE
                    specializations.visibility = View.GONE
                    address.visibility = View.GONE
                }
                CurrentAccountType.ATHLETE -> {
                    accountType.visibility = View.VISIBLE
                    athleteFirstname.visibility = View.VISIBLE
                    athleteLastname.visibility = View.VISIBLE
                    organizationName.visibility = View.GONE
                    nickname.visibility = View.VISIBLE
                    gender.visibility = View.VISIBLE
                    birthDate.visibility = View.VISIBLE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.VISIBLE
                    specializations.visibility = View.GONE
                    address.visibility = View.VISIBLE

                    nickname.setTitle(getString(R.string.athlete_nickname))
                    nickname.setHint(getString(R.string.hint_athlete_nickname))
                }
                CurrentAccountType.ORGANIZATION -> {
                    accountType.visibility = View.VISIBLE
                    athleteFirstname.visibility = View.GONE
                    athleteLastname.visibility = View.GONE
                    organizationName.visibility = View.VISIBLE
                    nickname.visibility = View.VISIBLE
                    gender.visibility = View.GONE
                    birthDate.visibility = View.GONE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.GONE
                    specializations.visibility = View.VISIBLE
                    address.visibility = View.VISIBLE

                    nickname.setTitle(getString(R.string.organization_nickname))
                    nickname.setHint(getString(R.string.hint_organization_nickname))
                }
            }
        }
    }

    private fun getCurrentData(): ProfileSettingsViewItem {
        return ProfileSettingsViewItem(
            accountType = when (currentAccountType) {
                CurrentAccountType.ATHLETE -> UserType.ATHLETE
                CurrentAccountType.ORGANIZATION -> UserType.ORGANIZATION
                else -> null
            },
            name = when (currentAccountType) {
                CurrentAccountType.ATHLETE -> {
                    binding.athleteFirstname.getText() + " " + binding.athleteLastname.getText()
                }
                CurrentAccountType.ORGANIZATION -> {
                    binding.organizationName.getText()
                }
                else -> null
            },
            nickname = binding.nickname.getText(),
            description = binding.description.getText(),
            gender = when (binding.gender.getCurrentValue()) {
                getString(R.string.male) -> GenderType.MALE
                getString(R.string.female) -> GenderType.FEMALE
                else -> null
            },
            birthDate = binding.birthDate.getDateMillis(),
            categories = when (currentAccountType) {
                CurrentAccountType.ATHLETE -> {
                    Categories.getCategoriesFromLocalized(
                        context!!,
                        binding.interests.getCheckedDataMap()
                    )
                }
                CurrentAccountType.ORGANIZATION -> {
                    Categories.getCategoriesFromLocalized(
                        context!!,
                        binding.interests.getCheckedDataMap()
                    )
                }
                else -> null
            },
            position = getPosition(),
            profilePhotoUri = profilePhoto
        )
    }

    private fun getPosition(): Position? {
        val addressText = binding.address.getText()
        return YandexMaps.getPosition(context!!, addressText)
    }

    private fun observeViewModel() {
        viewModel.saveHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fpssLoading.root.visibility = View.VISIBLE
                    binding.fpssError.root.visibility = View.GONE
                    binding.fpssContentBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fpssLoading.root.visibility = View.GONE
                    binding.fpssError.root.visibility = View.GONE
                    binding.fpssContentBlock.visibility = View.VISIBLE
                }
                is DataHolder.ERROR -> {
                    binding.fpssLoading.root.visibility = View.GONE
                    binding.fpssError.root.visibility = View.VISIBLE
                    binding.fpssContentBlock.visibility = View.GONE

                    binding.fpssError.veText.text = holder.failure.message
                    binding.fpssError.veTryAgain.setOnClickListener {
                        viewModel.signUp(email, password, getCurrentData())
                    }
                }
            }

            viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
                findNavController().navigate(R.id.action_profileSettingsSignUpFragment_to_tabsFragment)
            }

            viewModel.toastMessageEvent.observeEvent(viewLifecycleOwner) {
                when (it) {
                    ProfileSettingsSignUpViewModel.ExceptionType.EMAIL_ALREADY_EXISTS -> toast(
                        context,
                        getString(R.string.error_account_with_this_email_exists)
                    )
                    ProfileSettingsSignUpViewModel.ExceptionType.NICKNAME_ALREADY_EXISTS -> toast(
                        context,
                        getString(R.string.error_account_with_this_nickname_exists)
                    )
                    else -> toast(context, getString(R.string.error))
                }
            }
        }
    }

    private fun onSaveButtonPressed() {
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.signUp(email, password, getCurrentData())
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    enum class CurrentAccountType {
        NONE,
        ATHLETE,
        ORGANIZATION
    }

    private fun getEmailArgument(): String = args.email
    private fun getPasswordArgument(): String = args.password

////////////////////

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            val pickedPhoto = data.data
            profilePhoto = pickedPhoto.toString()

            setAvatar(profilePhoto, context!!, binding.avatar)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}