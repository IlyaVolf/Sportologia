package com.thesis.sportologia.ui.services.adapters

import androidx.core.view.isVisible
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ItemExerciseBinding
import com.thesis.sportologia.model.services.entities.Exercise
import com.thesis.sportologia.ui.base.BaseViewHolder
import com.thesis.sportologia.utils.Localization
import com.thesis.sportologia.utils.concatList

class ExercisesViewHolder(
    private val binding: ItemExerciseBinding,
    private val onItemClick: (Exercise) -> Unit,
    private val itemId: Int,
) : BaseViewHolder<Exercise>(binding) {

    override fun bindItem(item: Exercise) {

        binding.root.setOnClickListener {
            onItemClick(item)
        }

        binding.itemExerciseSplitter.isVisible = itemId != 0

        binding.exerciseNameBlock.text = item.name
        binding.exercisePropertiesBlock.text = parseProperties(item)
    }

    private fun parseProperties(item: Exercise): String {
        val res = StringBuilder("")

        res.append(item.setsNumber).append(" ").append(getString(R.string.exercise_of_sets))
        res.append(splittingDot)
        res.append(item.setsNumber).append(" ").append(getString(R.string.exercise_of_reps))
        res.append(splittingDot)
        val regularityLocalized = item.regularity.map {
            Localization.convertExerciseRegularityEnumToLocalized(
                context,
                it
            )
        }
        res.append(concatList(regularityLocalized, ", "))
        if (item.photosUris.isNotEmpty()) {
            res.append(item.photosUris.size).append(" ").append(getString(R.string.exercise_photo))
        }

        return res.toString()
    }

    private val splittingDot = " " + context.getString(R.string.split_dot) + " "

}
