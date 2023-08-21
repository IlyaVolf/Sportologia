package com.thesis.sportologia.ui._obsolete

/**import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentCreatePostBinding
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.ui.posts.create_edit_post_screen.CreateEditPostFragment
import com.thesis.sportologia.ui.base.BaseFragment
import com.thesis.sportologia.ui.views.OnToolbarBasicAction
import com.thesis.sportologia.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : BaseFragment(R.layout.fragment_create_post) {

    override val viewModel by viewModels<CreatePostViewModel>()

    private val photosUrls = mutableListOf<String>()

    private lateinit var binding: FragmentCreatePostBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CreateEditPostFragment.TEXT_KEY, binding.text.text.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        binding.toolbar.setListener {
            when (it) {
                OnToolbarBasicAction.LEFT -> onCancelButtonPressed()
                OnToolbarBasicAction.RIGHT -> onCreateButtonPressed()
            }
        }

        val savedText = savedInstanceState?.getString(CreateEditPostFragment.TEXT_KEY)
        if (savedText != null) {
            binding.text.setText(savedText)
        }

        observeGoBackEvent()
        observeToastMessageEvent()

        return binding.root
    }

    private fun onCancelButtonPressed() {
        createDialog()
    }

    private fun onCreateButtonPressed() {
        viewModel.createPost(binding.text.text.toString(), photosUrls)
    }

    private fun createDialog() {
        // TODO диалоги из utils

        val builder = AlertDialog.Builder(context, R.style.DialogStyleBasic)
        builder.setTitle(getString(R.string.ask_cancel_post_warning))
        //builder.setMessage(getString(R.string.ask_cancel_post_warning))
        builder.setNegativeButton(getString(R.string.action_delete)) { dialog, _ ->
            goBack(false)
            dialog.cancel()
        }
        builder.setNeutralButton(getString(R.string.action_back)) { dialog, _ ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()

        dialog.getButton(BUTTON_NEGATIVE)
            .setTextColor(context!!.getColor(R.color.purple_medium))
        dialog.getButton(BUTTON_NEUTRAL)
            .setTextColor(context!!.getColor(R.color.purple_medium))

        dialog.getButton(BUTTON_NEGATIVE).isAllCaps = false
        dialog.getButton(BUTTON_NEUTRAL).isAllCaps = false
    }

    override fun observeViewModel() {
        viewModel.holder.observe(viewLifecycleOwner) { holder ->
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
    }

    override fun setupViews() {
        super.setupViews()
        binding.fcpError.veTryAgain.setOnClickListener {
            viewModel.createPost(binding.text.text.toString(), photosUrls)
        }
    }

    private fun goBack(isCreated: Boolean) {
        requireActivity().supportFragmentManager.setFragmentResult(
            REQUEST_CODE,
            bundleOf(IS_CREATED to isCreated)
        )
        findNavController().navigateUp()
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        goBack(true)
    }

    private fun observeToastMessageEvent() =
        viewModel.toastMessageEvent.observeEvent(viewLifecycleOwner) {
            val errorText =
                when (it) {
                    CreatePostViewModel.ErrorType.EMPTY_POST -> getString(R.string.error_empty_post)
                }

            Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        }

    companion object {
        const val REQUEST_CODE = "IS_CREATED_REQUEST_CODE"
        const val IS_CREATED = "IS_CREATED"
    }
}*/