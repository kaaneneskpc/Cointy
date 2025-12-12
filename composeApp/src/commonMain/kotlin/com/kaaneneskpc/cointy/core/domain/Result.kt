package com.kaaneneskpc.cointy.core.domain

sealed interface Result<out Data, out Err : Error> {
    data class Success<out Data>(val data: Data) : Result<Data, Nothing>
    data class Error<out Err : com.kaaneneskpc.cointy.core.domain.Error>(val error: Err) :
        Result<Nothing, Err>
}

inline fun <T, Err : Error, Res> Result<T, Err>.map(map: (T) -> Res): Result<Res, Err> {
    return when (this) {
        is Result.Success -> Result.Success(map(data))
        is Result.Error -> Result.Error(error)
    }
}

fun <T, Err : Error> Result<T, Err>.asEmptyDataResult(): EmptyResult<Err> {
    return map { }
}

inline fun <T, Err : Error> Result<T, Err>.onSuccess(action: (T) -> Unit): Result<T, Err> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, Err : Error> Result<T, Err>.onError(action: (Err) -> Unit): Result<T, Err> {
    return when (this) {
        is Result.Error -> {
            action(error)
            this
        }

        is Result.Success -> this
    }
}

typealias EmptyResult<Err> = Result<Unit, Err>