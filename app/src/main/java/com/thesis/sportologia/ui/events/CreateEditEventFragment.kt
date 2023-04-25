package com.thesis.sportologia.ui.events

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
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
import com.thesis.sportologia.databinding.FragmentCreateEditEventBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
import com.thesis.sportologia.ui.events.entities.toCreateEditItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import com.yandex.runtime.Runtime.getApplicationContext
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CreateEditEventFragment : BaseFragment(R.layout.fragment_create_edit_event) {

    // TODO SAVE ON STATE, т.к. текст при повороте / смене темы затирается
    // TODO photosUrls
    // TODO remove empty string at the end of the event

    @Inject
    lateinit var factory: CreateEditEventViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(eventId)
    }

    private val args by navArgs<CreateEditEventFragmentArgs>()

    private var eventId: String? = null
    private var isDataReceived = false
    private var currentEventCreateEditItem: EventCreateEditItem =
        EventCreateEditItem.getEmptyInstance()

    private lateinit var mode: Mode
    private lateinit var binding: FragmentCreateEditEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditEventBinding.inflate(inflater, container, false)

        initMode()
        initRender()
        initAddPhotosButton()

        observeGoBackEvent()
        observeToastMessageEvent()

        return binding.root
    }

    private fun initRender() {
        initMode()
        initToolbar()
        initCategoriesSelector()
        initRetryButton()
    }

    private fun initMode() {
        eventId = getEventIdArg()

        mode = if (eventId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
    }

    private fun initToolbar() {
        when (mode) {
            Mode.CREATE -> {
                binding.fceeToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fceeToolbar.setTitle(getString(R.string.create_event))
                binding.fceeToolbar.setRightButtonText(getString(R.string.action_create))
            }
            Mode.EDIT -> {
                binding.fceeToolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.fceeToolbar.setTitle(getString(R.string.event))
                binding.fceeToolbar.setRightButtonText(getString(R.string.action_save))
            }
        }

        binding.fceeToolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onCancelButtonPressed()
                OnToolbarBasicAction.RIGHT -> onSaveButtonPressed()
            }
        }
    }

    private fun initCategoriesSelector() {
        binding.fceeCategories.setListener { }
    }

    private fun renderSelectedCategories(categories: Map<String, Boolean>?) {
        val categoriesMap = categories ?: Categories.emptyCategoriesMap
        val categoriesLocalizedMap = Categories.getLocalizedCategories(context!!, categoriesMap)
        binding.fceeCategories.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.event_categories_hint)
        )
    }

    private fun initRetryButton() {
        binding.fceeError.veTryAgain.setOnClickListener {
            onSaveButtonPressed()
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

    private fun renderData() {
        renderSelectedCategories(currentEventCreateEditItem.categories)

        if (currentEventCreateEditItem.name != null) {
            binding.fceeName.setText(currentEventCreateEditItem.name!!)
        }
        if (currentEventCreateEditItem.description != null) {
            binding.fceeDescription.setText(currentEventCreateEditItem.description!!)
        }
        if (currentEventCreateEditItem.priceString != null) {
            binding.fceePrice.setText(formatPrice(currentEventCreateEditItem.priceString!!))
        }
        if (currentEventCreateEditItem.position != null) {
            val address = getAddress()
            if (address != null) {
                binding.fceeAddress.setText(address)
            }
        }
        if (currentEventCreateEditItem.dateFrom != null) {
            binding.fceeDate.setDateAndTime(
                currentEventCreateEditItem.dateFrom!!,
                currentEventCreateEditItem.dateTo
            )
        }
    }

    private fun getEventIdArg(): String? = args.eventId.eventId

    private fun getCurrentData() {
        with(currentEventCreateEditItem) {
            name = binding.fceeName.getText()
            description = binding.fceeDescription.getText()
            dateFrom = binding.fceeDate.getDateFromMillis()
            dateTo = binding.fceeDate.getDateToMillis()
            position = getPosition()
            priceString = binding.fceePrice.getText()
            currency = getCurrencyByAbbreviation(context!!, R.string.ruble_abbreviation)!!
            categories = Categories.getCategoriesFromLocalized(
                context!!,
                binding.fceeCategories.getCheckedDataMap()
            )
        }
    }

    private fun getPosition(): Position? {
        val addressText = binding.fceeAddress.getText()
        return YandexMaps.getPosition(context!!, addressText)
    }

    private fun getAddress(): String? {
        return YandexMaps.getAddress(context!!, currentEventCreateEditItem.position)
    }

    private fun onCancelButtonPressed() {
        createCancelDialog()
    }

    private fun onSaveButtonPressed() {
        getCurrentData()
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(currentEventCreateEditItem)
        }
    }

    private fun createCancelDialog() {
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
                    binding.fceeLoading.root.visibility = VISIBLE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = VISIBLE
                }
                is DataHolder.ERROR -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = VISIBLE
                    binding.fceeEventBlock.visibility = GONE

                    binding.fceeError.veText.text = holder.failure.message
                    binding.fceeError.veTryAgain.setOnClickListener {
                        onSaveButtonPressed()
                    }
                }
            }
        }

        viewModel.eventHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fceeLoading.root.visibility = VISIBLE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = VISIBLE

                    // Мы не будем рендерить инфу, если она уже отрендерена
                    if (holder.data != null && !isDataReceived) {
                        currentEventCreateEditItem = holder.data.toCreateEditItem()
                        isDataReceived = true
                    }
                    renderData()
                }
                is DataHolder.ERROR -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = VISIBLE
                    binding.fceeEventBlock.visibility = GONE

                    binding.fceeError.veText.text = holder.failure.message
                    binding.fceeError.veTryAgain.setOnClickListener {
                        viewModel.getEvent()
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

    private fun observeToastMessageEvent() =
        viewModel.toastMessageEvent.observeEvent(viewLifecycleOwner) {
            val errorText =
                when (it) {
                    CreateEditEventViewModel.ErrorType.INCORRECT_PRICE -> getString(R.string.error_price_event)
                    CreateEditEventViewModel.ErrorType.INCORRECT_DATE -> getString(R.string.error_event_incorrect_date)
                    CreateEditEventViewModel.ErrorType.INCORRECT_ADDRESS -> getString(R.string.error_event_incorrect_address)
                    else -> getString(R.string.error_event_empty_fields)
                }

            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        }

    enum class Mode {
        CREATE,
        EDIT
    }

    // TODO parcelable
    data class EventId(
        val eventId: String?
    ) : Serializable

    companion object {
        /*data class EditResult(
            val isSaved: Boolean,
            val eventId: Long?,
            val text: String?,
        ) : Serializable*/

        const val EVENT_KEY = "EVENT_KEY"

        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE_EVENT"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE_EVENT"
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
            currentEventCreateEditItem.photosUrls.add(pickedPhoto.toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}