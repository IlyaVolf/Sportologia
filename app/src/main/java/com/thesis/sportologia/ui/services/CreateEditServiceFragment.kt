package com.thesis.sportologia.ui.services

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentCreateEditServiceBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.services.adapters.ExercisesAdapter
import com.thesis.sportologia.ui.services.entities.ExerciseCreateEditItem
import com.thesis.sportologia.ui.services.entities.ServiceCreateEditItem
import com.thesis.sportologia.ui.services.entities.toCreateEditItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class CreateEditServiceFragment : BaseFragment(R.layout.fragment_create_edit_service) {

    @Inject
    lateinit var factory: CreateEditServiceViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(serviceId)
    }

    private val args by navArgs<CreateEditServiceFragmentArgs>()

    private var serviceId: Long? = null
    private var isDataReceived = false
    private var currentServiceCreateEditItem: ServiceCreateEditItem = ServiceCreateEditItem.getEmptyInstance()
    private val exercisesAdapter by lazy { ExercisesAdapter(onExercisePressed) }

    private lateinit var mode: Mode
    private lateinit var binding: FragmentCreateEditServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditServiceBinding.inflate(inflater, container, false)

        initMode()
        initRender()
        initServiceTypeSelector()
        initResultsProcessing()

        observeToastMessageService()
        observeGoBackEvent()

        return binding.root
    }

    private fun initRender() {
        initToolbar()
        initCategoriesSelector()
        initCreateExerciseButton()
    }

    private fun initCreateExerciseButton() {
        binding.fcesAddExercise.setOnClickListener {
            onCreateExercisePressed()
        }
    }

    private fun initMode() {
        serviceId = getServiceIdArg()

        mode = if (serviceId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
    }

    private fun initToolbar() {
        when (mode) {
            Mode.CREATE -> {
                binding.fcesToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fcesToolbar.setTitle(getString(R.string.create_service))
                binding.fcesToolbar.setRightButtonText(getString(R.string.action_create))
            }
            Mode.EDIT -> {
                binding.fcesToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fcesToolbar.setTitle(getString(R.string.service))
                binding.fcesToolbar.setRightButtonText(getString(R.string.action_save))
            }
        }

        binding.fcesToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onCancelButtonPressed()
                OnToolbarBasicAction.RIGHT -> onSaveButtonPressed()
            }
        }
    }

    private fun initResultsProcessing() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditExerciseFragment.IS_CREATED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val exercise = data.getSerializable(CreateEditExerciseFragment.IS_CREATED) as ExerciseCreateEditItem?
                ?: return@setFragmentResultListener
            currentServiceCreateEditItem.exercises.add(exercise)
            setExercises(currentServiceCreateEditItem.exercises)
            Log.d("abcdef", "${currentServiceCreateEditItem}, ${exercise}")
            exercisesAdapter.notifyDataSetChanged()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditExerciseFragment.IS_EDITED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val exercise = data.getSerializable(CreateEditExerciseFragment.IS_EDITED) as ExerciseCreateEditItem?
                ?: return@setFragmentResultListener
            currentServiceCreateEditItem.exercises[currentServiceCreateEditItem.exercises.indexOfFirst { it.id == exercise.id }] =
                exercise
            setExercises(currentServiceCreateEditItem.exercises)
            Log.d("abcdef", "${currentServiceCreateEditItem}, ${exercise}")
            exercisesAdapter.notifyDataSetChanged()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditExerciseFragment.IS_DELETED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val exerciseId = data.getLong(CreateEditExerciseFragment.IS_DELETED)
            currentServiceCreateEditItem.exercises.removeIf { it.id == exerciseId }
            setExercises(currentServiceCreateEditItem.exercises)
            exercisesAdapter.notifyDataSetChanged()
        }
    }

    private fun initCategoriesSelector() {
        binding.fcesAims.setListener { }
    }

    private fun renderSelectedCategories(categories: Map<String, Boolean>?) {
        val categoriesMap = categories ?: TrainingProgrammesCategories.emptyCategoriesMap
        val categoriesLocalizedMap =
            TrainingProgrammesCategories.getLocalizedCategories(context!!, categoriesMap)
        binding.fcesAims.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.service_aims_hint)
        )
    }

    private fun initServiceTypeSelector() {
        val options = AssociativeList(
            listOf(
                Pair(
                    getString(R.string.service_training_program),
                    ServiceType.TRAINING_PROGRAM
                ),
            )
        )

        binding.fcesType.initAdapter(options.getFirsts())
    }

    private fun renderData() {
        renderSelectedCategories(currentServiceCreateEditItem.categories)

        if (currentServiceCreateEditItem.name != null) {
            binding.fcesName.setText(currentServiceCreateEditItem.name!!)
        }
        if (currentServiceCreateEditItem.type != null) {
            binding.fcesType.setItem(
                Localization.convertServiceTypeEnumToLocalized(
                    context!!,
                    currentServiceCreateEditItem.type!!
                )
            )
        }
        if (currentServiceCreateEditItem.generalDescription != null) {
            binding.fcesGeneralDescription.setText(currentServiceCreateEditItem.generalDescription!!)
        }
        if (currentServiceCreateEditItem.priceString != null) {
            binding.fcesPrice.setText(formatPrice(currentServiceCreateEditItem.priceString!!))
        }
        if (currentServiceCreateEditItem.detailedDescription != null) {
            binding.fcesDetailedDescription.setText(currentServiceCreateEditItem.detailedDescription!!)
        }
        setExercises(currentServiceCreateEditItem.exercises)
    }

    private fun setExercises(exercises: List<ExerciseCreateEditItem>) {
        binding.exercisesList.isVisible = exercises.isNotEmpty()
        binding.itemAddExerciseSplitter.isVisible = exercises.isNotEmpty()

        binding.exercisesList.adapter = exercisesAdapter
        exercisesAdapter.setupItems(exercises)
    }

    private fun getServiceIdArg(): Long? = args.serviceId.value

    private fun onCancelButtonPressed() {
        createDialogCancel()
    }

    private fun onCreateExercisePressed() {
        val direction =
            CreateEditServiceFragmentDirections.actionCreateEditServiceFragmentToCreateEditExerciseFragment(
                null
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

    private val onExercisePressed: (Exercise) -> Unit = { exercise ->
        getCurrentData()
        val direction =
            CreateEditServiceFragmentDirections.actionCreateEditServiceFragmentToCreateEditExerciseFragment(
                exercise.toCreateEditItem()
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

    private fun getCurrentData() {
        with(currentServiceCreateEditItem) {
            name = binding.fcesName.getText()
            generalDescription = binding.fcesGeneralDescription.getText()
            priceString = binding.fcesPrice.getText()
            currency = getCurrencyByAbbreviation(context!!, R.string.ruble_abbreviation)
            categories =
                TrainingProgrammesCategories.getCategoriesFromLocalized(
                    context!!,
                    binding.fcesAims.getCheckedDataMap()
                )
            type = Localization.convertServiceTypeLocalizedToEnum(
                context!!,
                binding.fcesType.getCurrentValue()
            )
            detailedDescription = binding.fcesDetailedDescription.getText()
        }
    }

    private fun onSaveButtonPressed() {
        getCurrentData()
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(currentServiceCreateEditItem)
        }
    }

    private fun createDialogCancel() {
        val messageText = getString(R.string.ask_cancel_event_warning)

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

    override fun observeViewModel() {
        viewModel.saveHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fcesLoading.root.visibility = View.VISIBLE
                    binding.fcesError.root.visibility = View.GONE
                    binding.fcesServiceBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fcesLoading.root.visibility = View.GONE
                    binding.fcesError.root.visibility = View.GONE
                    binding.fcesServiceBlock.visibility = View.VISIBLE
                }
                is DataHolder.ERROR -> {
                    binding.fcesLoading.root.visibility = View.GONE
                    binding.fcesError.root.visibility = View.VISIBLE
                    binding.fcesServiceBlock.visibility = View.GONE

                    binding.fcesError.veText.text = holder.failure.message
                    binding.fcesError.veTryAgain.setOnClickListener {
                        viewModel.onSaveButtonPressed(currentServiceCreateEditItem!!)
                    }
                }
            }
        }

        viewModel.serviceHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                is DataHolder.INIT -> {
                    binding.fcesLoading.root.visibility = View.GONE
                    binding.fcesError.root.visibility = View.GONE
                    binding.fcesServiceBlock.visibility = View.GONE
                }
                is DataHolder.LOADING -> {
                    binding.fcesLoading.root.visibility = View.VISIBLE
                    binding.fcesError.root.visibility = View.GONE
                    binding.fcesServiceBlock.visibility = View.GONE
                }
                is DataHolder.READY -> {
                    binding.fcesLoading.root.visibility = View.GONE
                    binding.fcesError.root.visibility = View.GONE
                    binding.fcesServiceBlock.visibility = View.VISIBLE

                    // Мы не будем рендерить инфу, если она уже отрендерена
                    if (holder.data != null && !isDataReceived) {
                        currentServiceCreateEditItem = holder.data.toCreateEditItem()
                        isDataReceived = true
                    }
                    renderData()
                }
                is DataHolder.ERROR -> {
                    binding.fcesLoading.root.visibility = View.GONE
                    binding.fcesError.root.visibility = View.VISIBLE
                    binding.fcesServiceBlock.visibility = View.GONE

                    binding.fcesError.veText.text = holder.failure.message
                    binding.fcesError.veTryAgain.setOnClickListener {
                        viewModel.getService()
                    }
                }
            }
        }
    }

    private fun goBack(isSaved: Boolean) {
        sendResult(isSaved)
        findNavController().navigateUp()
    }

    private fun sendResult(isSaved: Boolean) {
        if (mode == Mode.CREATE) {
            requireActivity().supportFragmentManager.setFragmentResult(
                IS_CREATED_REQUEST_CODE,
                bundleOf(IS_CREATED to isSaved)
            )
        } else if (mode == Mode.EDIT) {
            requireActivity().supportFragmentManager.setFragmentResult(
                IS_EDITED_REQUEST_CODE,
                bundleOf(IS_EDITED to isSaved)
            )
        }
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        goBack(true)
    }

    private fun observeToastMessageService() =
        viewModel.toastMessageService.observeEvent(viewLifecycleOwner) {
            val errorText =
                when (it) {
                    CreateEditServiceViewModel.ErrorType.INCORRECT_PRICE -> getString(R.string.error_price_event)
                    else -> getString(R.string.error_event_empty_fields)
                }

            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        }

    enum class Mode {
        CREATE,
        EDIT
    }

    // TODO parcelable
    data class ServiceId(
        val value: Long?
    ) : Serializable

    companion object {
        const val SERVICE_KEY = "SERVICE_KEY"

        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE_SERVICE"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE_SERVICE"
    }
}