package com.thesis.sportologia.ui

import android.os.Bundle
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
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseFragment
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

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onBackButtonPressed()
                OnToolbarBasicAction.RIGHT -> onAcquireButtonPressed()
            }
        }
    }

    private fun initGeneralListeners(serviceDetailedViewItem: ServiceDetailedViewItem) {
        binding.serviceStatsBlock.setOnClickListener {
            //
        }

        binding.serviceOrganizerBlock.setOnClickListener {
            onAuthorPressed(serviceDetailedViewItem.authorId)
        }

        binding.serviceStar.setOnClickListener {
            //setFavs(!serviceDetailedViewItem.isFavourite)
            viewModel.onToggleFavouriteFlag()
        }

        binding.servicePhotosBlockGeneral.setOnClickListener { }
    }


    private fun initRetryListener() {
        binding.serviceViewLoadState.flpError.veTryAgain.setOnClickListener {
            viewModel.getService()
        }
    }

    private fun renderService(serviceDetailedViewItem: ServiceDetailedViewItem) {
        if (serviceDetailedViewItem.isAcquired) {
            binding.toolbar.setRightButtonText(getString(R.string.action_acquire))
        } else {
            binding.toolbar.setRightButtonText(null)
        }

        renderGeneralInfo(serviceDetailedViewItem)
        if (serviceDetailedViewItem.isAcquired) {
            //binding.detailedContent.visibility = VISIBLE
            renderDetailedInfo(serviceDetailedViewItem)
        } else {
            //binding.detailedContent.visibility = GONE
            renderDetailedInfo(serviceDetailedViewItem)
        }
    }

    private fun renderGeneralInfo(serviceDetailedViewItem: ServiceDetailedViewItem) {
        initGeneralListeners(serviceDetailedViewItem)

        setCategories(
            TrainingProgrammesCategories.getLocalizedTrainingProgrammesCategories(
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

        val adapter = ExercisesAdapter(onExercisePressed)
        binding.exercisesList.adapter = adapter
        adapter.setupItems(serviceDetailedViewItem.exercises)
    }

    override fun observeViewModel() {
        viewModel.serviceHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpError.root.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.serviceContentBlock.visibility = VISIBLE
                    binding.serviceViewLoadState.root.visibility = GONE

                    renderService(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.serviceContentBlock.visibility = GONE
                    binding.serviceViewLoadState.root.visibility = VISIBLE
                    binding.serviceViewLoadState.flpLoading.root.visibility = GONE
                    binding.serviceViewLoadState.flpError.root.visibility = VISIBLE

                    toast(context, holder.failure.message ?: getString(R.string.error))
                }
                else -> {}
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


    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

    private fun onAcquireButtonPressed() {
        viewModel.acquireService()
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

    private fun setRating(rating: Float) {
        binding.serviceRatingAverage.text = rating.toString()
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
            binding.serviceTextSpaceBottomDetailed.visibility = GONE
        } else {
            binding.serviceTextSpaceBottomDetailed.visibility = VISIBLE
        }
    }

}