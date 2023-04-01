package com.thesis.sportologia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentServiceBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.model.services.entities.ServiceDetailed
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.posts.entities.PostListItem
import com.thesis.sportologia.ui.services.CreateEditServiceFragment
import com.thesis.sportologia.ui.services.adapters.ExercisesAdapter
import com.thesis.sportologia.ui.services.entities.ServiceDetailedViewItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class ServiceFragment : BaseFragment(R.layout.fragment_service) {

    @Inject
    lateinit var factory: ServiceViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(serviceId)
    }

    private val args by navArgs<ServiceFragmentArgs>()

    private lateinit var mode: Mode
    private lateinit var binding: FragmentServiceBinding
    private var serviceId by Delegates.notNull<Long>()

    private fun getServiceIdArg(): Long = args.serviceId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)
        serviceId = getServiceIdArg()

        initToolbar()
        initRetryListener()
        initResultsProcessing()

        observeGoBackEvent()

        return binding.root
    }

    private fun initMode(serviceDetailedViewItem: ServiceDetailedViewItem) {
        if (serviceDetailedViewItem.authorId == CurrentAccount().id) {
            mode = Mode.OWN_SERVICE
            return
        }

        mode = if (serviceDetailedViewItem.isAcquired) {
            Mode.ACQUIRED_SERVICE
        } else {
            Mode.NOT_ACQUIRED_SERVICE
        }
    }

    private fun initToolbar() {
        binding.toolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> goBack(false)
                OnToolbarBasicAction.RIGHT ->
                    when (mode) {
                        Mode.OWN_SERVICE -> {}
                        Mode.NOT_ACQUIRED_SERVICE -> onAcquireButtonPressed()
                        Mode.ACQUIRED_SERVICE -> {}
                    }
            }
        }
    }

    private fun initGeneralListeners(serviceDetailedViewItem: ServiceDetailedViewItem) {
        binding.serviceStatsBlock.setOnClickListener {
        }

        binding.serviceOrganizerBlock.setOnClickListener {
            onAuthorPressed(serviceDetailedViewItem.authorId)
        }

        binding.serviceStar.setOnClickListener {
            viewModel.onToggleFavouriteFlag()
        }

        binding.servicePhotosBlockGeneral.setOnClickListener { }
    }

    private fun initResultsProcessing() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            CreateEditServiceFragment.IS_EDITED_REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val isSaved = data.getBoolean(CreateEditServiceFragment.IS_EDITED)
            if (isSaved) {
                viewModel.getService()
            }
        }
    }


    private fun initRetryListener() {
        binding.serviceViewLoadState.flpError.veTryAgain.setOnClickListener {
            viewModel.getService()
        }
    }

    private fun renderService(serviceDetailedViewItem: ServiceDetailedViewItem) {
        initMode(serviceDetailedViewItem)
        renderGeneralInfo(serviceDetailedViewItem)

        when (mode) {
            Mode.ACQUIRED_SERVICE -> {
                binding.detailedContent.visibility = VISIBLE
                renderDetailedInfo(serviceDetailedViewItem)
                binding.toolbar.setRightButtonText(null)
            }
            Mode.NOT_ACQUIRED_SERVICE -> {
                binding.detailedContent.visibility = GONE
                binding.toolbar.setRightButtonText(getString(R.string.action_acquire))
            }
            Mode.OWN_SERVICE -> {
                binding.detailedContent.visibility = VISIBLE
                renderDetailedInfo(serviceDetailedViewItem)
                binding.toolbar.setRightButtonText(null)
            }
        }
    }

    private fun renderGeneralInfo(serviceDetailedViewItem: ServiceDetailedViewItem) {
        initGeneralListeners(serviceDetailedViewItem)

        binding.postMore.setOnClickListener {
            onMoreButtonPressed(serviceDetailedViewItem)
        }
        setCategories(
            TrainingProgrammesCategories.getLocalizedCategories(
                context!!,
                serviceDetailedViewItem.categories
            )
        )
        setServiceName(serviceDetailedViewItem.name)
        setServiceType(serviceDetailedViewItem.serviceType)
        setGeneralDescription(serviceDetailedViewItem.generalDescription)
        setAuthorName(serviceDetailedViewItem.authorName)
        setAuthorType(
            Localization.convertUserTypeEnumToLocalized(
                context!!,
                serviceDetailedViewItem.authorType
            )
        )
        setAuthorAvatar(serviceDetailedViewItem.profilePictureUrl)
        setPrice(serviceDetailedViewItem.price, serviceDetailedViewItem.currency)
        setAcquiredNumber(serviceDetailedViewItem.acquiredNumber)
        setReviewsNumber(serviceDetailedViewItem.reviewsNumber)
        setRating(serviceDetailedViewItem.rating)
        setFavs(serviceDetailedViewItem.isFavourite)
        setGeneralPhotos(serviceDetailedViewItem.generalPhotosUrls)
    }

    private fun renderDetailedInfo(serviceDetailedViewItem: ServiceDetailedViewItem) {
        setDetailedDescription(serviceDetailedViewItem.detailedDescription)
        setDetailedPhotos(serviceDetailedViewItem.detailedPhotosUrls)
        setExercises(serviceDetailedViewItem.exercises)
    }

    override fun observeViewModel() {
        viewModel.serviceHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    Log.d("abcdef", "DataHolder.LOADING")
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpError.root.visibility = GONE
                }
                is DataHolder.READY -> {
                    Log.d("abcdef", "DataHolder.READY")
                    binding.serviceContentBlock.visibility = VISIBLE
                    binding.serviceViewLoadState.root.visibility = GONE

                    renderService(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = GONE
                    binding.serviceViewLoadState.flpError.root.visibility = VISIBLE

                    binding.serviceViewLoadState.flpError.veTryAgain.setOnClickListener {
                        viewModel.getService()
                    }

                    toast(context, holder.failure.message ?: getString(R.string.error))
                }
                else -> {}
            }
        }

        viewModel.deleteHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpError.root.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.serviceContentBlock.visibility = VISIBLE
                    binding.serviceViewLoadState.root.visibility = GONE
                }
                is DataHolder.ERROR -> {
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = GONE
                    binding.serviceViewLoadState.flpError.root.visibility = VISIBLE

                    toast(context, holder.failure.message ?: getString(R.string.error))

                    binding.serviceViewLoadState.flpError.veTryAgain.setOnClickListener {
                        viewModel.deleteService()
                    }
                }
            }
        }

        viewModel.toastMessageEvent.observe(viewLifecycleOwner) { holder ->
            val toastText = when (holder.get()) {
                ServiceViewModel.ResponseType.ACQUIRED_SUCCESSFULLY -> getString(R.string.service_acquired_successfully)
                ServiceViewModel.ResponseType.FAVS_ERROR -> getString(R.string.error)
                ServiceViewModel.ResponseType.ACQUIRE_ERROR -> getString(R.string.error_acquiring)
                null -> getString(R.string.error)
            }
            toast(context, toastText)
        }
    }

    private fun onAuthorPressed(userIdToGo: String) {
        if (userIdToGo != CurrentAccount().id) {
            val direction =
                ServiceFragmentDirections.actionServiceFragmentToProfileNested(
                    userIdToGo
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
        } else {
            requireActivity().supportFragmentManager.setFragmentResult(
                ServicesAcquiredFragment.GO_TO_OWN_PROFILE_REQUEST_CODE,
                bundleOf()
            )
        }
    }

    private val onExercisePressed: (Exercise) -> Unit = {
        /*val direction =
            ServiceNotAcquiredFragmentDirections.actionServiceNotAcquiredFragmentToProfileNested(
                userIdToGo
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
            })*/
    }

    private fun createOnDeleteDialog() {
        createSimpleDialog(
            context!!,
            null,
            ResourcesUtils.getString(R.string.ask_delete_service_warning),
            ResourcesUtils.getString(R.string.action_delete),
            { _, _ ->
                run {
                    viewModel.deleteService()
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

    private fun onMoreButtonPressed(service: ServiceDetailedViewItem) {
        val actionsMore: Array<Pair<String, DialogOnClickAction?>> =
            if (service.authorId == CurrentAccount().id) {
                arrayOf(
                    Pair(ResourcesUtils.getString(R.string.action_edit)) { _, _ ->
                        run {
                            onEditButtonPressed()
                        }
                    },
                    Pair(ResourcesUtils.getString(R.string.action_delete)) { _, _ ->
                        run {
                            createOnDeleteDialog()
                        }
                    },
                )
            } else {
                arrayOf(
                    Pair(ResourcesUtils.getString(R.string.action_report)) { _, _ -> }
                )
            }

        createSpinnerDialog(
            context!!,
            null,
            null,
            actionsMore
        )
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        goBack(true)
    }

    private fun goBack(isSaved: Boolean) {
        sendResult(isSaved)
        findNavController().navigateUp()
    }

    private fun sendResult(isSaved: Boolean) {
        requireActivity().supportFragmentManager.setFragmentResult(
            IS_DELETED_REQUEST_CODE,
            bundleOf(IS_DELETED to isSaved)
        )
    }

    private fun onAcquireButtonPressed() {
        viewModel.acquireService()
    }

    private fun onEditButtonPressed() {
        val direction = TabsFragmentDirections.actionTabsFragmentToCreateEditServiceFragment(
            CreateEditServiceFragment.ServiceId(serviceId)
        )

        findTopNavController().navigate(direction,
            navOptions {
                anim {
                    enter = R.anim.enter
                    exit = R.anim.exit
                    popEnter = R.anim.pop_enter
                    popExit = R.anim.pop_exit
                }
            })
    }

    private fun setFavs(isAddedToFavs: Boolean) {
        if (isAddedToFavs) {
            binding.serviceStar.setImageResource(R.drawable.icon_star_pressed)
            binding.serviceStar.setColorFilter(context!!.getColor(R.color.background_inverted))
        } else {
            binding.serviceStar.setImageResource(R.drawable.icon_star_unpressed)
            binding.serviceStar.setColorFilter(context!!.getColor(R.color.background_inverted))
        }
    }

    private fun setServiceName(name: String) {
        binding.serviceName.text = name
    }

    private fun setServiceType(serviceType: ServiceType) {
        binding.serviceType.text =
            Localization.convertServiceTypeEnumToLocalized(context!!, serviceType)
    }

    private fun setGeneralDescription(description: String) {
        binding.serviceGeneralDescription.text = description
    }

    private fun setAuthorName(username: String) {
        binding.serviceUserName.text = username
    }

    private fun setAuthorType(userType: String) {
        binding.serviceUserType.text = userType
    }

    private fun setAuthorAvatar(uriImage: String?) {
        setAvatar(uriImage, context!!, binding.serviceAvatar)
    }

    private fun setAcquiredNumber(acquiredNumber: Int) {
        binding.serviceAcquiredNumber.text = formatQuantity(acquiredNumber)
    }

    private fun setReviewsNumber(reviewsNumber: Int) {
        binding.serviceReviewsNumber.text = formatQuantity(reviewsNumber)
    }

    private fun setRating(rating: Float?) {
        binding.serviceRatingAverage.text = rating?.toString() ?: "-"
    }

    private fun setPrice(price: Float, currency: String) {
        binding.servicePrice.text = getPriceWithCurrency(context!!, price, currency)
    }

    private fun setCategories(categories: Map<String, Boolean>) {
        val categoriesString = concatMap(categories, ", ")
        if (categoriesString != "") {
            binding.serviceCategories.text = categoriesString
        } else {
            binding.serviceCategories.text = context!!.getString(R.string.categories_not_specified)
        }
    }

    private fun setGeneralPhotos(photosURIs: List<String>?) {
        binding.servicePhotosBlockGeneral.uploadPhotos(photosURIs ?: listOf())
        if (photosURIs == null || photosURIs.isEmpty()) {
            binding.serviceTextSpaceBottomGeneral.visibility = GONE
        } else {
            binding.serviceTextSpaceBottomGeneral.visibility = VISIBLE
        }
    }

    private fun setDetailedDescription(description: String) {
        binding.serviceDetailedDescription.text = description
    }

    private fun setDetailedPhotos(photosURIs: List<String>?) {
        binding.servicePhotosBlockDetailed.uploadPhotos(photosURIs ?: listOf())
        if (photosURIs == null || photosURIs.isEmpty()) {
            binding.servicePhotosBlockBeforeDetailed.visibility = GONE
            binding.servicePhotosBlockAfterDetailed.visibility = GONE
        } else {
            binding.servicePhotosBlockBeforeDetailed.visibility = VISIBLE
            binding.servicePhotosBlockAfterDetailed.visibility = VISIBLE
        }
    }

    private fun setExercises(exercises: List<Exercise>) {
        if (exercises.isEmpty()) {
            binding.exercisesListEmpty.visibility = VISIBLE
            binding.exercisesList.visibility = GONE
        } else {
            binding.exercisesListEmpty.visibility = GONE
            binding.exercisesList.visibility = VISIBLE
        }

        val adapter = ExercisesAdapter(onExercisePressed)
        binding.exercisesList.adapter = adapter
        adapter.setupItems(exercises)
    }

    enum class Mode {
        OWN_SERVICE, ACQUIRED_SERVICE, NOT_ACQUIRED_SERVICE
    }

    companion object {
        const val IS_DELETED_REQUEST_CODE = "IS_DELETED_REQUEST_CODE_SERVICE"
        const val IS_DELETED = "IS_DELETED"
    }

}