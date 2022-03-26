package tech.svehla.gratitudejournal.data.repository

import kotlinx.coroutines.flow.*
import tech.svehla.gratitudejournal.common.Resource
import timber.log.Timber

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow<Resource<ResultType>> {
    emit(Resource.Loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            Timber.e("NetworkBoundResource fetch failed: ${throwable.message}")
            onFetchFailed(throwable)
            // TODO - add proper message
            query().map { Resource.Error(throwable.localizedMessage ?: "NBR Error", it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}