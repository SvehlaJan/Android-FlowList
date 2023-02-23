package tech.svehla.gratitudejournal.core.domain.util

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason

interface ErrorHandler {
    fun processError(throwable: Throwable): ErrorReason
}