package tech.svehla.gratitudejournal.repository

import kotlinx.coroutines.flow.*
import timber.log.Timber

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow<Resource<ResultType>> {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.success(it) }
        } catch (throwable: Throwable) {
            Timber.e("NetworkBoundResource fetch failed: ${throwable.message}")
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, it) }
        }
    } else {
        query().map { Resource.success(it) }
    }

    emitAll(flow)
}