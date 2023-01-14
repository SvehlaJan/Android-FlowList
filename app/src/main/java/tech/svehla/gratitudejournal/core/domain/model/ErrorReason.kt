package tech.svehla.gratitudejournal.core.domain.model

sealed class ErrorReason {
    object Network : ErrorReason()
    object NotFound : ErrorReason()
    object AccessDenied : ErrorReason()
    object ServiceUnavailable : ErrorReason()
    object Unknown : ErrorReason()
}
