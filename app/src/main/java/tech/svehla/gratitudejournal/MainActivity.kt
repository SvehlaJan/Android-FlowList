package tech.svehla.gratitudejournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.giphy.sdk.ui.Giphy
import dagger.hilt.android.AndroidEntryPoint
import tech.svehla.gratitudejournal.ui.main.MainScreen
import tech.svehla.gratitudejournal.ui.theme.GratitudeJournalTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this, BuildConfig.GIPHY_API_KEY)

        setContent {
            GratitudeJournalTheme {
                MainScreen()
            }
        }
    }
}