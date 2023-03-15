package com.thesis.sportologia.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentCreateEditPostBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.createSimpleDialog
import com.thesis.sportologia.utils.observeEvent
import com.thesis.sportologia.utils.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject


@AndroidEntryPoint
class CreateEditPostFragment : BaseFragment(R.layout.fragment_create_edit_post) {

    // TODO SAVE ON STATE, т.к. текст при повороте / смене темы затирается
    // TODO photosUrls
    // TODO remove empty string at the end of the post

    @Inject
    lateinit var factory: CreateEditPostViewModel.Factory

    override val viewModel by viewModelCreator {
        factory.create(postId)
    }

    private var postId: Long? = null

    private lateinit var mode: Mode

    private val args by navArgs<CreateEditPostFragmentArgs>()

    // TODO
    private val photosUrls = mutableListOf<String>()

    private var savedText: String? = null

    private lateinit var binding: FragmentCreateEditPostBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, binding.text.text.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditPostBinding.inflate(inflater, container, false)

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
                binding.toolbar.setTitle(getString(R.string.create_post))
                binding.toolbar.setRightButtonText(getString(R.string.action_create))
            }
            Mode.EDIT -> {
                binding.toolbar.setLeftButtonText(getString(R.string.action_cancel))
                binding.toolbar.setTitle(getString(R.string.edit_post))
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
        postId = getPostIdArg()

        mode = if (postId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
    }

    private fun setEditableText(post: Post?) {
        if (post == null) return

        if (savedText == null) {
            binding.text.setText(post.text)
        } else {
            binding.text.setText(savedText)
        }
    }


    private fun getPostIdArg(): Long? = args.postId.postId

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
        val messageText = getString(R.string.ask_cancel_post_warning)

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

        viewModel.postHolder.observe(viewLifecycleOwner) { holder ->
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
            bundleOf(EDIT_RESULT to EditResult(isSaved, postId, binding.text.text.toString()))
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
                    CreateEditPostViewModel.ErrorType.EMPTY_POST -> getString(R.string.error_empty_post)
                }

            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        }

    enum class Mode {
        CREATE,
        EDIT
    }

    // TODO parcelable
    data class PostId(
        val postId: Long?
    ) : Serializable

    companion object {
        /*data class EditResult(
            val isSaved: Boolean,
            val postId: Long?,
            val text: String?,
        ) : Serializable*/

        const val TEXT_KEY = "TEXT_KEY"

        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE"
    }

}