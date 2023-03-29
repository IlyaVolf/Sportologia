package com.thesis.sportologia.ui._obsolete

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
import com.thesis.sportologia.databinding.FragmentServiceNotAcquiredBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.services.entities.ServiceViewItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

/**@AndroidEntryPoint
class ServiceNotAcquiredFragment : BaseFragment(R.layout.fragment_service_not_acquired) {

    @Inject
    lateinit var factory: ServiceNotAcquiredViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(serviceId)
    }

    private val args by navArgs<ServiceNotAcquiredFragmentArgs>()

    private lateinit var binding: FragmentServiceNotAcquiredBinding
    private var serviceId by Delegates.notNull<Long>()

    private fun getServiceIdArg(): Long = args.serviceId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceNotAcquiredBinding.inflate(inflater, container, false)
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

    private fun initListeners(serviceFullItem: ServiceViewItem) {
        binding.serviceStatsBlock.setOnClickListener {
            //
        }

        binding.serviceOrganizerBlock.setOnClickListener {
            onAuthorPressed(serviceFullItem.authorId)
        }

        binding.serviceStar.setOnClickListener {
            //setFavs(!serviceFullItem.isFavourite)
            viewModel.onToggleFavouriteFlag()
        }

        binding.servicePhotosBlock.setOnClickListener { }
    }

    private fun initRetryListener() {
        binding.serviceViewLoadState.flpError.veTryAgain.setOnClickListener {
            viewModel.getService()
        }
    }

    private fun renderService(serviceFullItem: ServiceViewItem) {
        renderGeneralInfo(serviceFullItem)
    }

    private fun renderGeneralInfo(serviceFullItem: ServiceViewItem) {
        initListeners(serviceFullItem)

        setCategories(
            TrainingProgrammesCategories.getLocalizedTrainingProgrammesCategories(
                context!!,
                serviceFullItem.categories
            )
        )
        setServiceName(serviceFullItem.name)
        setServiceType(serviceFullItem.serviceType)
        setDescription(serviceFullItem.generalDescription)
        setAuthorName(serviceFullItem.authorName)
        setAuthorType(
            Localization.convertUserTypeEnumToLocalized(
                context!!,
                serviceFullItem.authorType
            )
        )
        setAuthorAvatar(serviceFullItem.profilePictureUrl)
        setPrice(serviceFullItem.price, serviceFullItem.currency)
        setAcquiredNumber(serviceFullItem.acquiredNumber)
        setReviewsNumber(serviceFullItem.reviewsNumber)
        setRating(serviceFullItem.rating)
        setFavs(serviceFullItem.isFavourite)
        setPhotos(serviceFullItem.generalPhotosUrls)
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
                ServiceNotAcquiredViewModel.ErrorType.FAVS_ERROR -> getString(R.string.error)
                ServiceNotAcquiredViewModel.ErrorType.ACQUIRE_ERROR -> getString(R.string.error_acquiring)
                null -> getString(R.string.error)
            }
            toast(context, toastText)
        }
    }

    private fun onAuthorPressed(userIdToGo: String) {
        if (userIdToGo != CurrentAccount().id) {
            val direction =
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
                })
        } else {
            requireActivity().supportFragmentManager.setFragmentResult(
                ServicesAcquiredFragment.GO_TO_OWN_PROFILE_REQUEST_CODE,
                bundleOf()
            )
        }
    }

    private fun onBackButtonPressed() {
        findNavController().navigateUp()
    }

    private fun onAcquireButtonPressed() {
        viewModel.acquireService()
        findNavController().navigateUp()
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

    private fun setDescription(description: String) {
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

    private fun setPhotos(photosURIs: List<String>?) {
        binding.servicePhotosBlock.uploadPhotos(photosURIs ?: listOf())
        if (photosURIs == null || photosURIs.isEmpty()) {
            binding.serviceTextSpaceBottom.visibility = GONE
        } else {
            binding.serviceTextSpaceBottom.visibility = VISIBLE
        }
    }

}*/