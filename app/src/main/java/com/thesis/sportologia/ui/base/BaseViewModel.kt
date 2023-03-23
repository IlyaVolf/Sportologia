package com.thesis.sportologia.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.sportologia.R
import com.thesis.sportologia.model.BackendException
import com.thesis.sportologia.model.ConnectionException
import com.thesis.sportologia.utils.MutableLiveEvent
import com.thesis.sportologia.utils.logger.Logger
import com.thesis.sportologia.utils.publishEvent
import com.thesis.sportologia.utils.share
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(
    val logger: Logger
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
    val showErrorMessageResEvent = _showErrorMessageResEvent.share()

    private val _showErrorMessageEvent = MutableLiveEvent<String>()
    val showErrorMessageEvent = _showErrorMessageEvent.share()

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: ConnectionException) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R
                    .string.network_connection_error)
            } catch (e: BackendException) {
                logError(e)
                _showErrorMessageEvent.publishEvent(e.message ?: "")
            } catch (e: Exception) {
                logError(e)
                _showErrorMessageResEvent.publishEvent(R.string.network_internal_error)
            }
        }
    }

    fun logError(e: Throwable) {
        logger.error(javaClass.simpleName, e)
    }
}
