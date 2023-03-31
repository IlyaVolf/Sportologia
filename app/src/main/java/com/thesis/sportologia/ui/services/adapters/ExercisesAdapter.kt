package com.thesis.sportologia.ui.services.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemExerciseBinding
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.ui.base.BaseAdapter
import com.thesis.sportologia.ui.base.BaseViewHolder

class ExercisesAdapter(
    private val onItemClick: (Exercise) -> Unit
) : BaseAdapter<BaseViewHolder<Exercise>, Exercise>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<Exercise> =
        ExercisesViewHolder(
            binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick,
            parent.id
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Exercise> =
        takeViewHolder(parent)
}
