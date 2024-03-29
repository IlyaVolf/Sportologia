package com.thesis.sportologia.ui.services.adapters

import androidx.core.view.isVisible
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemExerciseBinding
import com.thesis.sportologia.model.services.entities.ExerciseDataEntity
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.utils.Regularity
import com.thesis.sportologia.utils.concatMap

class ExercisesViewHolder(
    private val binding: ItemExerciseBinding,
    private val onItemClick: (ExerciseDataEntity) -> Unit,
) : BaseViewHolder<ExerciseDataEntity>(binding) {

    override fun bindItem(item: ExerciseDataEntity) {

        binding.root.setOnClickListener {
            onItemClick(item)
        }

        binding.itemExerciseSplitter.isVisible = position != 0

        binding.exerciseNameBlock.text = item.name
        binding.exercisePropertiesBlock.text = parseProperties(item)
    }

    private fun parseProperties(item: ExerciseDataEntity): String {
        val res = StringBuilder("")

        res.append(item.setsNumber).append(" ").append(getString(R.string.exercise_of_sets))
        res.append(splittingDot)
        res.append(item.repsNumber).append(" ").append(getString(R.string.exercise_of_reps))
        res.append(splittingDot)
        val regularitiesLocalized = Regularity.getLocalizedRegularities(context, item.regularity)
        res.append(concatMap(regularitiesLocalized, ", "))
        if (item.photosUris.isNotEmpty()) {
            res.append(splittingDot)
            res.append(item.photosUris.size).append(" ").append(getString(R.string.exercise_photo))
        }

        return res.toString()
    }

    private val splittingDot = " " + context.getString(R.string.split_dot) + " "

}
