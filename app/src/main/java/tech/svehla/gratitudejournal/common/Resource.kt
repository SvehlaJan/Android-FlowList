package tech.svehla.gratitudejournal.common

import tech.svehla.gratitudejournal.domain.model.ErrorEntity

//sealed class Resource<out T> {
//    abstract val data: T?
//    abstract val message: String?
//
//    data class Loading<T>(
//        override val data: T? = null,
//        override val message: String? = null
//    ) : Resource<T>()
//
//    data class Success<T>(
//        override val data: T,
//        override val message: String? = null
//    ) : Resource<T>()
//
//    data class Error<T>(
//        override val data: T? = null,
//        override val message: String?
//    ) : Resource<T>()
//}

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()

    data class Loading<T>(val data: T? = null): Resource<T>()

    data class Error<T>(val error: ErrorEntity) : Resource<T>()
}