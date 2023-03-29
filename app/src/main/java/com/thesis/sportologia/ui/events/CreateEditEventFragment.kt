package com.thesis.sportologia.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentCreateEditEventBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
import com.thesis.sportologia.ui.posts.CreateEditPostViewModel
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
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

    // TODO photosUrls
    private val photosUrls = mutableListOf<String>()

    private var eventId: Long? = null
    private var currentEventCreateEditItem: EventCreateEditItem? = null

    private lateinit var mode: Mode
    private lateinit var binding: FragmentCreateEditEventBinding

    // TODO onSaveInstanceState
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EVENT_KEY, currentEventCreateEditItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditEventBinding.inflate(inflater, container, false)

        initMode()
        initRender()
        initCurrentEventCreateEditItem(savedInstanceState)

        observeGoBackEvent()
        observeToastMessageEvent()

        return binding.root
    }

    private fun initRender() {
        initMode()
        initToolbar()
        initCategoriesSelector()
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

    private fun renderSelectedCategories(event: Event?) {
        val categoriesMap = event?.categories ?: Categories.emptyCategoriesMap
        val categoriesLocalizedMap = hashMapOf<String, Boolean>()
        categoriesMap.map {
            categoriesLocalizedMap.put(
                Categories.convertEnumToCategory(context, it.key)!!,
                it.value
            )
        }
        binding.fceeCategories.initMultiChoiceList(
            categoriesLocalizedMap,
            getString(R.string.event_categories_hint)
        )
    }

    private fun initCurrentEventCreateEditItem(savedInstanceState: Bundle?) {
        currentEventCreateEditItem =
            savedInstanceState?.getSerializable(EVENT_KEY) as EventCreateEditItem?
    }

    private fun renderData(event: Event?) {
        renderSelectedCategories(event)

        if (event == null) return

        binding.fceeName.setText(event.name)
        binding.fceeDescription.setText(event.description)
        binding.fceePrice.setText(formatPrice(event.price.toString()))
        binding.fceeAddress.setText(event.address.toString())
        binding.fceeDate.setDateAndTime(event.dateFrom, event.dateTo)
    }

    private fun getEventIdArg(): Long? = args.eventId.eventId

    private fun onCancelButtonPressed() {
        createDialog()
    }

    private fun onSaveButtonPressed() {
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(
                EventCreateEditItem(
                    binding.fceeName.getText(),
                    binding.fceeDescription.getText(),
                    binding.fceeDate.getDateFromMillis(),
                    binding.fceeDate.getDateToMillis(),
                    null,
                    binding.fceePrice.getText(),
                    getCurrencyByAbbreviation(context!!, R.string.ruble_abbreviation)!!,
                    binding.fceeCategories.getCheckedDataMap(Categories.emptyCategoriesMap.keys.toTypedArray()),
                    photosUrls
                )
            )
        }
    }

    private fun createDialog() {
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
                }
            }
        }

        viewModel.eventHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.fceeLoading.root.visibility = VISIBLE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = GONE
                    binding.fceeEventBlock.visibility = VISIBLE

                    renderData(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.fceeLoading.root.visibility = GONE
                    binding.fceeError.root.visibility = VISIBLE
                    binding.fceeEventBlock.visibility = GONE

                    binding.fceeError.veText.text = holder.failure.message
                }
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        binding.fceeError.veTryAgain.setOnClickListener {
            onSaveButtonPressed()
        }
    }

    private fun goBack(isSaved: Boolean) {
        sendResult(isSaved)

        /*val bundle = if (isSaved) {
            bundleOf(EDIT_RESULT to EditResult(isSaved, eventId, binding.text.text.toString()))
        } else {
            bundleOf(EDIT_RESULT to EditResult(isSaved, null, null))
        }
        requireActivity().supportFragmentManager.setFragmentResult(REQUEST_CODE, bundle)*/

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
        val eventId: Long?
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

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE"
    }

}