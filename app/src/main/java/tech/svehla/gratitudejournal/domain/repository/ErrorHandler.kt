package tech.svehla.gratitudejournal.domain.repository

import tech.svehla.gratitudejournal.domain.model.ErrorEntity

interface ErrorHandler {
    fun getError(throwable: Throwable): ErrorEntity
}