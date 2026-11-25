package tech.svehla.gratitudejournal

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import tech.svehla.gratitudejournal.core.presentation.model.JournalEntryVO
import tech.svehla.gratitudejournal.core.presentation.ui.theme.GratitudeJournalTheme
import tech.svehla.gratitudejournal.detail.presentation.DetailScreenContent
import tech.svehla.gratitudejournal.history.presentation.HistoryEmptyScreen
import tech.svehla.gratitudejournal.history.presentation.HistoryListScreen
import tech.svehla.gratitudejournal.settings.presentation.SettingsScreen
import tech.svehla.gratitudejournal.settings.presentation.SettingsScreenState

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    GratitudeJournalTheme {
        Surface(content = content)
    }
}

@PreviewTest
@Preview(name = "Light", showBackground = true)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
fun HistoryEmptyScreenScreenshotTest() {
    PreviewSurface {
        HistoryEmptyScreen()
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun HistoryListScreenScreenshotTest() {
    PreviewSurface {
        HistoryListScreen(
            items = listOf(
                JournalEntryVO(
                    date = "2026-11-25",
                    firstNote = "Morning coffee on the balcony",
                    secondNote = "Finished AGP 9.5 upgrade",
                    thirdNote = "Evening walk",
                    gifUrl = null,
                    imageUrl = null,
                ),
                JournalEntryVO(
                    date = "2026-11-24",
                    firstNote = "Good chat with a friend",
                    secondNote = "Cooked dinner",
                    thirdNote = "Read before bed",
                    gifUrl = null,
                    imageUrl = null,
                ),
            ),
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun SettingsScreenScreenshotTest() {
    PreviewSurface {
        SettingsScreen(
            state = SettingsScreenState(
                currentUser = null,
                isLoading = false,
                signInErrorMessage = null,
            ),
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun DetailScreenContentScreenshotTest() {
    PreviewSurface {
        DetailScreenContent(
            journalEntry = JournalEntryVO(
                date = "2026-11-25",
                firstNote = "Morning coffee on the balcony",
                secondNote = "Finished AGP 9.5 upgrade",
                thirdNote = "Evening walk",
                gifUrl = null,
                imageUrl = null,
            ),
        )
    }
}
