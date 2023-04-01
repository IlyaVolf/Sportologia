package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentExerciseBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.services.entities.ExerciseViewItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ExerciseFragment : BaseFragment(R.layout.fragment_exercise) {

    @Inject
    lateinit var factory: ExerciseViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(serviceId, exerciseId)
    }

    private val args by navArgs<ExerciseFragmentArgs>()

    private lateinit var binding: FragmentExerciseBinding
    private var serviceId by Delegates.notNull<Long>()
    private var exerciseId by Delegates.notNull<Long>()

    private fun getServiceIdArg(): Long = args.serviceId
    private fun getExerciseIdArg(): Long = args.exerciseId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)

        serviceId = getServiceIdArg()
        exerciseId = getExerciseIdArg()

        initToolbar()

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> goBack()
                else -> {}
            }
        }
    }

    private fun renderService(ExerciseViewItem: ExerciseViewItem) {
        setExerciseName(ExerciseViewItem.name)
        setDescription(ExerciseViewItem.description)
        setRegularity(ExerciseViewItem.regularity)
        setSetsNumber(ExerciseViewItem.setsNumber)
        setsRepsNumber(ExerciseViewItem.repsNumber)
        setPhotos(ExerciseViewItem.photosUris)
    }

    override fun observeViewModel() {
        viewModel.exerciseHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.exerciseContentBlock.visibility = GONE
                    binding.exerciseViewLoadState.root.visibility = VISIBLE
                    binding.exerciseViewLoadState.flpLoading.root.visibility = VISIBLE
                    binding.exerciseViewLoadState.flpError.root.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.exerciseContentBlock.visibility = VISIBLE
                    binding.exerciseViewLoadState.root.visibility = GONE

                    renderService(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.exerciseContentBlock.visibility = GONE
                    binding.exerciseViewLoadState.root.visibility = VISIBLE
                    binding.exerciseViewLoadState.flpLoading.root.visibility = GONE
                    binding.exerciseViewLoadState.flpError.root.visibility = VISIBLE

                    binding.exerciseViewLoadState.flpError.veTryAgain.setOnClickListener {
                        viewModel.getExercise()
                    }
                }
                else -> {}
            }
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun setExerciseName(name: String) {
        binding.exerciseName.text = name
    }

    private fun setDescription(description: String) {
        binding.exerciseDescription.text = description
    }

    private fun setRegularity(regularities: Map<String, Boolean>) {
        val regularitiesLocalized = Regularity.getLocalizedRegularities(context!!, regularities)
        binding.exerciseRegularity.text = concatMap(regularitiesLocalized, ", ")
    }

    private fun setSetsNumber(number: Int) {
        binding.exerciseSetsNumber.text = number.toString()
    }

    private fun setsRepsNumber(number: Int) {
        binding.exerciseRepsNumber.text = number.toString()
    }

    private fun setPhotos(photosURIs: List<String>?) {
        binding.exercisePhotosBlock.uploadPhotos(photosURIs ?: listOf())
        if (photosURIs == null || photosURIs.isEmpty()) {
            binding.exercisePhotosBlockSpace.visibility = GONE
        } else {
            binding.exercisePhotosBlockSpace.visibility = VISIBLE
        }
    }

}