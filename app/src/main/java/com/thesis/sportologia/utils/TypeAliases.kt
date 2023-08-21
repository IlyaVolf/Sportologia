package com.thesis.sportologia.utils

import androidx.lifecycle.MutableLiveData
import com.thesis.sportologia.model.DataHolder

typealias ObservableHolder<T> = MutableLiveData<DataHolder<T>>

typealias TryAgainAction = () -> Unit