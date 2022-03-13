package tech.svehla.gratitudejournal.repository

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val error: Throwable?
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> error(error: Throwable, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, error.message, error)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, null)
        }
    }
}