package com.thesis.sportologia.ui.services

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentCreateEditExerciseBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.services.entities.ExerciseCreateEditItem
import com.thesis.sportologia.ui.services.entities.toCreateEditItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditExerciseFragment : BaseFragment(R.layout.fragment_create_edit_exercise) {

    @Inject
    lateinit var factory: CreateEditExerciseViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(currentExerciseCreateEditItem)
    }

    private val args by navArgs<CreateEditExerciseFragmentArgs>()

    private var isDataReceived = false
    private lateinit var currentExerciseCreateEditItem: ExerciseCreateEditItem

    private lateinit var mode: Mode
    private lateinit var binding: FragmentCreateEditExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditExerciseBinding.inflate(inflater, container, false)

        initMode()
        initRender()
        initAddPhotosButton()

        observeToastMessageService()

        return binding.root
    }

    private fun initRender() {
        initToolbar()
        initRegularitySelector()
    }

    private fun initMode() {
        val originalExercise = getExerciseArg()

        mode = if (originalExercise == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }

        currentExerciseCreateEditItem =
            originalExercise?.toCreateEditItem() ?: ExerciseCreateEditItem.getEmptyInstance()
    }

    private fun initToolbar() {
        when (mode) {
            Mode.CREATE -> {
                binding.fcexToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fcexToolbar.setTitle(getString(R.string.create_exercise))
                binding.fcexToolbar.setRightButtonText(getString(R.string.action_create))
            }
            Mode.EDIT -> {
                binding.fcexToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fcexToolbar.setTitle(getString(R.string.exercise))
                binding.fcexToolbar.setRightButtonText(getString(R.string.action_save))
            }
        }

        binding.fcexToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onCancelButtonPressed()
                OnToolbarBasicAction.RIGHT -> onSaveButtonPressed()
            }
        }
    }

    private fun initAddPhotosButton() {
        binding.addPhotosButton.setOnClickListener {
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

    private fun initRegularitySelector() {
        binding.fcexRegularity.setListener { }
    }

    private fun renderSelectedRegularity(regularity: Map<String, Boolean>?) {
        val categoriesMap = regularity ?: Regularity.emptyRegularities
        val categoriesLocalizedMap =
            Regularity.getLocalizedRegularities(context!!, categoriesMap)
        binding.fcexRegularity.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.exercise_regularity_hint)
        )
    }

    private fun renderData() {
        renderSelectedRegularity(currentExerciseCreateEditItem.regularity)

        if (currentExerciseCreateEditItem.name != null) {
            binding.fcexName.setText(currentExerciseCreateEditItem.name!!)
        }
        if (currentExerciseCreateEditItem.description != null) {
            binding.fcexDescription.setText(currentExerciseCreateEditItem.description!!)
        }
        if (currentExerciseCreateEditItem.setsNumber != null) {
            binding.fcexSetsNumber.setText(currentExerciseCreateEditItem.setsNumber.toString())
        }
        if (currentExerciseCreateEditItem.repsNumber != null) {
            binding.fcexRepsNumber.setText(currentExerciseCreateEditItem.repsNumber.toString())
        }
        if (mode == Mode.EDIT) {
            binding.fcexDelete.visibility = VISIBLE
            binding.fcexDelete.setOnClickListener {
                onDeleteButtonPressed()
            }
        } else {
            binding.fcexDelete.visibility = GONE
        }
    }

    private fun getExerciseArg(): Exercise? = args.exercise

    private fun onCancelButtonPressed() {
        createDialogCancel()
    }

    private fun onDeleteButtonPressed() {
        createDialogDelete()
    }

    private fun getCurrentData() {
        with(currentExerciseCreateEditItem) {
            name = binding.fcexName.getText()
            description = binding.fcexDescription.getText()
            setsNumber = binding.fcexSetsNumber.getText()
            repsNumber = binding.fcexRepsNumber.getText()
            regularity = Regularity.getRegularitiesFromLocalized(
                context!!,
                binding.fcexRegularity.getCheckedDataMap()
            )
        }
    }

    private fun onSaveButtonPressed() {
        getCurrentData()
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(currentExerciseCreateEditItem!!)
        }
    }

    private fun createDialogCancel() {
        val messageText = getString(R.string.ask_cancel_exercise_warning)

        val neutralButtonText = getString(R.string.action_back)

        val negativeButtonText = when (mode) {
            Mode.CREATE -> getString(R.string.action_delete)
            Mode.EDIT -> getString(R.string.action_discard_changes)
        }

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

    private fun createDialogDelete() {
        createSimpleDialog(
            context!!,
            null,
            ResourcesUtils.getString(R.string.ask_delete_exercise_warning),
            ResourcesUtils.getString(R.string.action_delete),
            { _, _ ->
                run {
                    goBackAndDelete()
                }
            },
            ResourcesUtils.getString(R.string.action_cancel),
            { dialog, _ ->
                run {
                    dialog.cancel()
                }
            },
            null,
            null,
        )
    }

    override fun observeViewModel() {
        viewModel.saveHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fcexLoading.root.visibility = VISIBLE
                    binding.fcexError.root.visibility = GONE
                    binding.fcexExerciseBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fcexLoading.root.visibility = GONE
                    binding.fcexError.root.visibility = GONE
                    binding.fcexExerciseBlock.visibility = VISIBLE

                    goBack(true)
                }
                is DataHolder.ERROR -> {
                    binding.fcexLoading.root.visibility = GONE
                    binding.fcexError.root.visibility = VISIBLE
                    binding.fcexExerciseBlock.visibility = GONE

                    binding.fcexError.veText.text = holder.failure.message
                    binding.fcexError.veTryAgain.setOnClickListener {
                        viewModel.onSaveButtonPressed(currentExerciseCreateEditItem)
                    }
                }
            }
        }

        viewModel.exerciseHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                is DataHolder.INIT -> {}
                is DataHolder.LOADING -> {
                    binding.fcexLoading.root.visibility = VISIBLE
                    binding.fcexError.root.visibility = GONE
                    binding.fcexExerciseBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fcexLoading.root.visibility = GONE
                    binding.fcexError.root.visibility = GONE
                    binding.fcexExerciseBlock.visibility = VISIBLE

                    if (holder.data != null && !isDataReceived) {
                        currentExerciseCreateEditItem = holder.data
                        isDataReceived = true
                    }
                    renderData()
                }
                is DataHolder.ERROR -> {
                    binding.fcexLoading.root.visibility = GONE
                    binding.fcexError.root.visibility = VISIBLE
                    binding.fcexExerciseBlock.visibility = GONE

                    binding.fcexError.veText.text = holder.failure.message
                    binding.fcexError.veTryAgain.setOnClickListener {
                        viewModel.getExercise()
                    }
                }
            }
        }
    }

    private fun goBackAndDelete() {
        requireActivity().supportFragmentManager.setFragmentResult(
            IS_DELETED_REQUEST_CODE,
            bundleOf(IS_DELETED to currentExerciseCreateEditItem.id)
        )
        findNavController().navigateUp()
    }

    private fun goBack(isSaved: Boolean) {
        sendResult(isSaved)
        findNavController().navigateUp()
    }

    private fun sendResult(isSaved: Boolean) {
        if (!isSaved) return

        getCurrentData()
        if (mode == Mode.CREATE) {
            requireActivity().supportFragmentManager.setFragmentResult(
                IS_CREATED_REQUEST_CODE,
                bundleOf(IS_CREATED to currentExerciseCreateEditItem.toExercise())
            )
        } else if (mode == Mode.EDIT) {
            requireActivity().supportFragmentManager.setFragmentResult(
                IS_EDITED_REQUEST_CODE,
                bundleOf(IS_EDITED to currentExerciseCreateEditItem.toExercise())
            )
        }
    }

    private fun observeToastMessageService() =
        viewModel.toastMessageService.observeEvent(viewLifecycleOwner) {
            val errorText =
                when (it) {
                    else -> getString(R.string.error_event_empty_fields)
                }

            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        }

    enum class Mode {
        CREATE,
        EDIT
    }

    companion object {
        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"
        const val IS_DELETED = "IS_DELETED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE_EXERCISE"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE_EXERCISE"
        const val IS_DELETED_REQUEST_CODE = "IS_DELETED_REQUEST_CODE_EXERCISE"
    }

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

            currentExerciseCreateEditItem.photosUris.add(pickedPhoto.toString())
            Log.d("abcdef", "$currentExerciseCreateEditItem")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}