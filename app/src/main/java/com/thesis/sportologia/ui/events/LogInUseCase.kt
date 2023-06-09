package com.thesis.sportologia.ui.events

import android.util.Log
import com.thesis.sportologia.ui.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LogInUseCase(dispatcher: CoroutineDispatcher): UseCase<Int, Int>(dispatcher) {

    override suspend fun execute(param: Int): Int {
        Log.d("abcdef", "${param + 2}")
        return param + 2
    }

}