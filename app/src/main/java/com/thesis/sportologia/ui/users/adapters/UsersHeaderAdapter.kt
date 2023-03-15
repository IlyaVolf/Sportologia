package com.thesis.sportologia.ui.users.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.thesis.sportologia.utils.findTopNavController
import androidx.recyclerview.widget.RecyclerView
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentListUsersHeaderBinding
import com.thesis.sportologia.ui.TabsFragmentDirections
import com.thesis.sportologia.ui.views.OnSpinnerOnlyOutlinedActionListener

abstract class UsersHeaderAdapter(
    private val fragment: Fragment,
) : RecyclerView.Adapter<UsersHeaderAdapter.Holder>() {

    protected lateinit var binding: FragmentListUsersHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FragmentListUsersHeaderBinding.inflate(inflater, parent, false)

        return Holder(fragment, renderHeader, binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    abstract val renderHeader: () -> Unit

    class Holder(
        val fragment: Fragment,
        private val renderHeader: () -> Unit,
        private val viewBinding: FragmentListUsersHeaderBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind() {
            renderHeader()
        }

    }
}