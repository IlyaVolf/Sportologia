package com.thesis.sportologia.ui.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Базовый класс для UseCase
 *
 * UseCase будет выполнен на переданном dispatcher'е
 *
 * Результат возвращается в виде [kotlin.Result]
 *
 */
abstract class UseCase<in TParam, out TResult>(
    private val dispatcher: CoroutineDispatcher
) {
    /**
     * Реализация UseCase
     */
    protected abstract suspend fun execute(param: TParam): TResult

    /**
     * Выполняет UseCase
     */
    suspend operator fun invoke(param: TParam): Result<TResult> = withContext(dispatcher) {
        runCatching { execute(param) }
    }

    /**
     * UseCase с пустым результатом
     */
    abstract class EmptyResult<in TParam>(
        dispatcher: CoroutineDispatcher
    ) : UseCase<TParam, Unit>(dispatcher)

    /**
     * UseCase без параметра
     */
    abstract class NoParam<out TResult>(
        dispatcher: CoroutineDispatcher
    ) : UseCase<Unit, TResult>(dispatcher) {

        /**
         * Реализация UseCase
         */
        abstract suspend fun execute(): TResult

        /**
         * Метод-прокси для синтаксической чистоты
         */
        final override suspend fun execute(param: Unit) = execute()

        /**
         * Метод-прокси для синтаксической чистоты
         */
        suspend operator fun invoke() = invoke(Unit)

        /**
         * UseCase без параметра и с пустым результатом
         */
        abstract class EmptyResult(
            dispatcher: CoroutineDispatcher
        ) : NoParam<Unit>(dispatcher)
    }
}
