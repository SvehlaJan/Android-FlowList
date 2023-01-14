package tech.svehla.gratitudejournal.common

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()

    data class Loading<T>(val data: T? = null): Resource<T>()

    data class Error<T>(val error: Throwable) : Resource<T>()
}