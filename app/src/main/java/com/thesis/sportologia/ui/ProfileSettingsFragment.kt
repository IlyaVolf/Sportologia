package com.thesis.sportologia.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileSettingsBinding
import com.thesis.sportologia.databinding.FragmentProfileSettingsSignupBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.entities.GenderType
import com.thesis.sportologia.model.users.entities.User
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.ui.entities.ProfileSettingsViewItem
import com.thesis.sportologia.ui.posts.CreateEditPostFragment
import com.thesis.sportologia.ui.posts.entities.toCreateEditItem
import com.thesis.sportologia.ui.views.MultiChoiceBasicView
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.ui.views.SpinnerBasicView
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {

    private lateinit var binding: FragmentProfileSettingsBinding

    private lateinit var currentProfileSettings: ProfileSettingsViewItem

    private val viewModel by viewModels<ProfileSettingsViewModel>()
    private var profilePhoto: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)

        initToolbar()
        initProfilePhotoButton()
        initGenderSpinner()
        initInterestsMultiChoiceList()
        initSpecializationsMultiChoiceList()

        observeViewModel()

        return binding.root
    }

    private fun initToolbar() {
        binding.fpsToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> {
                    onCancelButtonPressed()
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

    private fun renderDataVisibility() {
        with(binding) {
            when (currentProfileSettings.accountType) {
                UserType.ATHLETE -> {
                    athleteFirstname.visibility = View.VISIBLE
                    athleteLastname.visibility = View.VISIBLE
                    organizationName.visibility = View.GONE
                    gender.visibility = View.VISIBLE
                    birthDate.visibility = View.VISIBLE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.VISIBLE
                    specializations.visibility = View.GONE
                    address.visibility = View.VISIBLE
                }
                UserType.ORGANIZATION -> {
                    athleteFirstname.visibility = View.GONE
                    athleteLastname.visibility = View.GONE
                    organizationName.visibility = View.VISIBLE
                    gender.visibility = View.GONE
                    birthDate.visibility = View.GONE
                    description.visibility = View.VISIBLE
                    interests.visibility = View.GONE
                    specializations.visibility = View.VISIBLE
                    address.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun renderData() {
        with (binding) {
            when (currentProfileSettings.accountType!!) {
                UserType.ATHLETE -> {
                    athleteFirstname.setText(currentProfileSettings.name!!.split(" ")[0])
                    athleteLastname.setText(currentProfileSettings.name!!.split(" ").getOrNull(1) ?: "")
                    description.setText(currentProfileSettings.description!!)
                    gender.setItem(
                        when (currentProfileSettings.gender!!) {
                            GenderType.MALE -> getString(R.string.male)
                            GenderType.FEMALE -> getString(R.string.female)
                        }
                    )
                    birthDate.setDateMillis(currentProfileSettings.birthDate!!)
                    setAvatar(currentProfileSettings.profilePhotoUri, context!!, avatar)
                    renderSelectedInterests(currentProfileSettings.categories!!)
                }
                UserType.ORGANIZATION -> {
                    organizationName.setText(currentProfileSettings.name!!)
                    description.setText(currentProfileSettings.description!!)
                    setAvatar(currentProfileSettings.profilePhotoUri, context!!, avatar)
                    renderSelectedSpecializations(currentProfileSettings.categories!!)
                }
            }
        }
    }

    private fun renderSelectedInterests(categories: Map<String, Boolean>) {
        val categoriesLocalizedMap =
            TrainingProgrammesCategories.getLocalizedCategories(context!!, categories)
        Log.d("abcdef", categoriesLocalizedMap.toString())
        binding.interests.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.hint_athlete_interests)
        )
    }

    private fun renderSelectedSpecializations(categories: Map<String, Boolean>) {
        val categoriesLocalizedMap =
            TrainingProgrammesCategories.getLocalizedCategories(context!!, categories)
        binding.specializations.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.hint_organization_specializations)
        )
    }

    private fun getCurrentData(): ProfileSettingsViewItem {
        return ProfileSettingsViewItem(
            accountType = currentProfileSettings.accountType,
            name = when (currentProfileSettings.accountType) {
                UserType.ATHLETE -> {
                    binding.athleteFirstname.getText() + " " + binding.athleteLastname.getText()
                }
                UserType.ORGANIZATION -> {
                    binding.organizationName.getText()
                }
                else -> null
            },
            nickname = currentProfileSettings.nickname,
            description = binding.description.getText(),
            gender = when (binding.gender.getCurrentValue()) {
                getString(R.string.male) -> GenderType.MALE
                getString(R.string.female) -> GenderType.FEMALE
                else -> null
            },
            birthDate = binding.birthDate.getDateMillis(),
            categories = when (currentProfileSettings.accountType) {
                UserType.ATHLETE -> {
                    Categories.getCategoriesFromLocalized(
                        context!!,
                        binding.interests.getCheckedDataMap()
                    )
                }
                UserType.ORGANIZATION -> {
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

    private fun onCancelButtonPressed() {
        createCancelDialog()
    }

    private fun createCancelDialog() {
        val messageText = getString(R.string.ask_cancel_post_warning)
        val neutralButtonText = getString(R.string.action_back)
        val negativeButtonText = getString(R.string.action_discard_changes)

        createSimpleDialog(
            context!!,
            null,
            messageText,
            negativeButtonText,
            { dialog, _ ->
                run {
                    goBack(false)
                    dialog.cancel()
                }
            },
            neutralButtonText,
            { dialog, _ ->
                run {
                    dialog.cancel()
                }
            },
            null,
            null,
        )
    }

    private fun observeViewModel() {
        viewModel.saveHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fpsLoading.root.visibility = View.VISIBLE
                    binding.fpsError.root.visibility = View.GONE
                    binding.fpsContentBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fpsLoading.root.visibility = View.GONE
                    binding.fpsError.root.visibility = View.GONE
                    binding.fpsContentBlock.visibility = View.VISIBLE

                    goBack(true)
                }
                is DataHolder.ERROR -> {
                    binding.fpsLoading.root.visibility = View.GONE
                    binding.fpsError.root.visibility = View.VISIBLE
                    binding.fpsContentBlock.visibility = View.GONE

                    binding.fpsError.veText.text = holder.failure.message
                    binding.fpsError.veTryAgain.setOnClickListener {
                        viewModel.onSaveButtonPressed(getCurrentData())
                    }
                }
            }
        }

        viewModel.profileSettingsHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fpsLoading.root.visibility = View.VISIBLE
                    binding.fpsError.root.visibility = View.GONE
                    binding.fpsContentBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fpsLoading.root.visibility = View.GONE
                    binding.fpsError.root.visibility = View.GONE
                    binding.fpsContentBlock.visibility = View.VISIBLE

                    currentProfileSettings = holder.data
                    renderDataVisibility()
                    renderData()
                }
                is DataHolder.ERROR -> {
                    binding.fpsLoading.root.visibility = View.GONE
                    binding.fpsError.root.visibility = View.VISIBLE
                    binding.fpsContentBlock.visibility = View.GONE

                    binding.fpsError.veText.text = holder.failure.message
                    binding.fpsError.veTryAgain.setOnClickListener {
                        viewModel.getProfileSettings()
                    }
                }
            }
        }

        viewModel.toastMessageEvent.observeEvent(viewLifecycleOwner) {
            when (it) {
                else -> toast(context, getString(R.string.error))
            }
        }
    }

    private fun onSaveButtonPressed() {
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(getCurrentData())
        }
    }

    private fun goBack(isSaved: Boolean) {
        //sendResult(isSaved)

        findNavController().navigateUp()
    }


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