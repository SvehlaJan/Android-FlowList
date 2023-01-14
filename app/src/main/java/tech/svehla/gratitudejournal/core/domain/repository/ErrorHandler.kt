package tech.svehla.gratitudejournal.core.domain.repository

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason

interface ErrorHandler {
    fun processError(throwable: Throwable): ErrorReason
}