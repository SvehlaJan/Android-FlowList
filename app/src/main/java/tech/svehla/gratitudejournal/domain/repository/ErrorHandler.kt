package tech.svehla.gratitudejournal.domain.repository

import tech.svehla.gratitudejournal.domain.model.ErrorReason

interface ErrorHandler {
    fun processError(throwable: Throwable): ErrorReason
}