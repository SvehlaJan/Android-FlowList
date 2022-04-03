package tech.svehla.gratitudejournal.common

sealed class Resource<out T> {
    abstract val data: T?
    abstract val message: String?

    data class Loading<T>(
        override val data: T? = null,
        override val message: String? = null
    ) : Resource<T>()

    data class Success<T>(
        override val data: T,
        override val message: String? = null
    ) : Resource<T>()

    data class Error<T>(
        override val data: T? = null,
        override val message: String?
    ) : Resource<T>()
}