package com.thesis.sportologia.ui.search.search_screen

import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.CurrentAccount
import com.thesis.sportologia.model.DataHolder
import com.thesis.sportologia.model.users.UsersRepository
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    logger: Logger
) : BaseViewModel(logger)