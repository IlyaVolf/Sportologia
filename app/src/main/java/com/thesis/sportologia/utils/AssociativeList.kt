package com.thesis.sportologia.utils

import com.thesis.sportologia.model.users.entities.FilterParamsUsers

class AssociativeList<T, R>(
    val list: List<Pair<T, R>>
) {

    fun getFirsts() = list.map { it.first }

    fun getAssociatedItemFirst(item: R) = list.first { item == it.second }.first

    fun getAssociatedItemSecond(item: T) = list.first { item == it.first }.second

}