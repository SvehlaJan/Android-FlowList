package tech.svehla.gratitudejournal.presentation.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    fun fromIsoToHumanReadableDate(date: String): String {
        return LocalDate
            .parse(date, DateTimeFormatter.ISO_DATE)
            .format(DateTimeFormatter.ofPattern("dd. MMMM yyyy"))
    }
}