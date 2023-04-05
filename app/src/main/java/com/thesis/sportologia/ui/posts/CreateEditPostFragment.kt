package com.thesis.sportologia.ui.posts

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.thesis.sportologia.databinding.FragmentCreateEditPostBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.posts.entities.Post
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.events.entities.EventCreateEditItem
import com.thesis.sportologia.ui.events.entities.toCreateEditItem
import com.thesis.sportologia.ui.posts.entities.PostCreateEditItem
import com.thesis.sportologia.ui.posts.entities.toCreateEditItem
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.*
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
    private var isDataReceived = false
    private var currentPostCreateEditItem: PostCreateEditItem =
        PostCreateEditItem.getEmptyInstance()

    private lateinit var mode: Mode

    private val args by navArgs<CreateEditPostFragmentArgs>()
    private lateinit var binding: FragmentCreateEditPostBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditPostBinding.inflate(inflater, container, false)

        initMode()
        initRender()

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

        initAddPhotosButton()

    }

    private fun initAddPhotosButton() {
        binding.addPhotosButton.setOnClickListener {
            Log.d("abcdef", "setListener")
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

    private fun initMode() {
        postId = getPostIdArg()

        mode = if (postId == null) {
            Mode.CREATE
        } else {
            Mode.EDIT
        }
    }

    private fun renderData() {
        if (currentPostCreateEditItem.text != null) {
            binding.text.setText(currentPostCreateEditItem.text!!)
        }
    }

    private fun getPostIdArg(): Long? = args.postId.postId

    private fun onCancelButtonPressed() {
        createDialog()
    }

    private fun getCurrentData() {
        with(currentPostCreateEditItem) {
            text = binding.text.text.toString()
        }
    }

    private fun onSaveButtonPressed() {
        getCurrentData()
        viewModel.saveHolder.value?.onNotLoading {
            viewModel.onSaveButtonPressed(currentPostCreateEditItem)
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
                DataHolder.INIT -> {}
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
                DataHolder.INIT -> {}
                DataHolder.LOADING -> {
                    binding.fcpLoading.root.visibility = VISIBLE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = GONE
                }
                is DataHolder.READY -> {
                    binding.fcpLoading.root.visibility = GONE
                    binding.fcpError.root.visibility = GONE
                    binding.textBlock.visibility = VISIBLE

                    if (holder.data != null && !isDataReceived) {
                        currentPostCreateEditItem = holder.data.toCreateEditItem()
                        isDataReceived = true
                    }
                    renderData()
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
                    CreateEditPostViewModel.ErrorType.EMPTY_TEXT -> getString(R.string.error_empty_post)
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
        const val TEXT_KEY = "TEXT_KEY"

        const val IS_CREATED = "IS_CREATED"
        const val IS_EDITED = "IS_EDITED"

        const val IS_CREATED_REQUEST_CODE = "IS_CREATED_REQUEST_CODE_POST"
        const val IS_EDITED_REQUEST_CODE = "IS_EDITED_REQUEST_CODE_POST"
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
            val pickedPhoto = data.data.toString()
            currentPostCreateEditItem.photosUrls = currentPostCreateEditItem.photosUrls + pickedPhoto
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}