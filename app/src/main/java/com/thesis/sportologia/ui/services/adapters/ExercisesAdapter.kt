package com.thesis.sportologia.ui.services.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thesis.sportologia.databinding.ItemExerciseBinding
import com.thesis.sportologia.model.services.entities.ExerciseDataEntity
import com.thesis.sportologia.ui.base.BaseAdapter
import com.thesis.sportologia.ui.base.BaseViewHolder

class ExercisesAdapter(
    private val onItemClick: (ExerciseDataEntity) -> Unit
) : BaseAdapter<BaseViewHolder<ExerciseDataEntity>, ExerciseDataEntity>() {

    override fun takeViewHolder(parent: ViewGroup): BaseViewHolder<ExerciseDataEntity> =
        ExercisesViewHolder(
            binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ExerciseDataEntity> =
        takeViewHolder(parent)
}
