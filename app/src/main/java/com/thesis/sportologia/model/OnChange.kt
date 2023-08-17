package com.thesis.sportologia.model

/**
 * Non-data class which allows passing the same reference to the
 * MutableStateFlow multiple times in a row.
 */
class OnChange<T>(val value: T)