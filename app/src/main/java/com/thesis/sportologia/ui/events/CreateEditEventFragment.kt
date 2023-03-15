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
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.events.entities.Event
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.createSimpleDialog
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject


/**@AndroidEntryPoint
class CreateEditEventFragment : BaseFragment(R.layout.fragment_create_edit_event) {

    // TODO SAVE ON STATE, т.к. текст при повороте / смене темы затирается
    // TODO photosUrls
    // TODO remove empty string at the end of the event

    @Inject
    lateinit var factory: CreateEditEventViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(eventId)
    }

    private var eventId: Long? = null

    private lateinit var mode: Mode

    private val args by navArgs<CreateEditEventFragmentArgs>()

    // TODO
    private val photosUrls = mutableListOf<String>()

    private var savedText: String? = null

    private lateinit var binding: FragmentCreateEditEventBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, binding.text.text.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditEventBinding.inflate(inflater, container, false)

        initMode()
        initRender()

        savedText = savedInstanceState?.getString(TEXT_KEY)
        observeGoBackEvent()
        observeToastMessageEvent()

        return binding.root
    }

    private fun initRender() {
        initMode()

        when (mode) {
            Mode.CREATE -> {
                binding.toolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.toolbar.setTitle(getString(R.string.create_event))
                binding.toolbar.setRightButtonText(getString(R.string.action_create))
            }
            Mode.EDIT -> {
                binding.toolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.toolbar.setTitle(getString(R.string.edit_event))
                binding.toolbar.setRightButtonText(getString(R.string.action_save))
            }
        }

        binding.toolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onCancelButtonPressed()
                OnToolbarBasicAction.RIGHT -> onSaveButtonPressed()
            }
        }

    }

    private fun initMode() {
        eventId = getEventIdArg()

        mode = if (eventId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
    }

    private fun setEditableText(event: Event?) {
        if (event == null) return

        if (savedText == null) {
            binding.text.setText(event.text)
        } else {
            binding.text.setText(savedText)
        }
    }


    private fun getEventIdArg(): Long? = args.eventId.eventId

    private fun onCancelButtonPressed() {
        createDialog()
    }


    private fun onSaveButtonPressed() {
        viewModel.saveHolder.observe(viewLifecycleOwner) { holder ->
            if (holder !is DataHolder.LOADING) {
                viewModel.onSaveButtonPressed(binding.text.text.toString(), photosUrls)
            }
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
                    binding.fcpLoading.root.visibility = VISIBLE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fcpLoading.root.visibility = GONE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = VISIBLE
                }
                is DataHolder.ERROR -> {
                    binding.fcpLoading.root.visibility = GONE
                    binding.fcpError.root.visibility = VISIBLE
                    binding.textBlock.visibility = GONE

                    binding.fcpError.veText.text = holder.failure.message
                }
            }
        }

        viewModel.eventHolder.observe(viewLifecycleOwner) { holder ->
            when (holder) {
                DataHolder.LOADING -> {
                    binding.fcpLoading.root.visibility = VISIBLE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fcpLoading.root.visibility = GONE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = VISIBLE

                    setEditableText(holder.data)
                }
                is DataHolder.ERROR -> {
                    binding.fcpLoading.root.visibility = GONE
                    binding.fcpError.root.visibility = VISIBLE
                    binding.textBlock.visibility = GONE

                    binding.fcpError.veText.text = holder.failure.message
                }
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        binding.fcpError.veTryAgain.setOnClickListener {
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
                    CreateEditEventViewModel.ErrorType.EMPTY_POST -> getString(R.string.error_empty_event)
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

        const val TEXT_KEY = "TEXT_KEY"

        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE"
    }

}*/