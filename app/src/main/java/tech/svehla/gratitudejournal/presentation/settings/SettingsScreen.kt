package tech.svehla.gratitudejournal.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.presentation.ui.components.SignInButton
import tech.svehla.gratitudejournal.presentation.ui.util.AuthResultContract

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val signInRequestCode = 1
    val authResultLauncher =
        rememberLauncherForActivityResult(contract = AuthResultContract()) { task ->
            viewModel.onGoogleSignInResult(task)
        }

    val state: SettingsScreenState by viewModel.state

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (state.currentUser != null) {
                    Text(
                        text = "Logged in as ${state.currentUser!!.email}",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(modifier = Modifier.align(Alignment.End), onClick = {
                        viewModel.signOut()
                    }) {
                        Text(text = "Sign out")
                    }
                } else {
                    Text(
                        text = "Sign in to sync your journal entries across devices",
                        style = MaterialTheme.typography.body1
                    )
                    Spacer(Modifier.height(32.dp))
                    SignInButton(
                        modifier = Modifier.align(Alignment.End),
                        text = "Sign in with Google",
                        loadingText = "Signing in...",
                        isLoading = state.isLoading,
                        icon = painterResource(id = R.drawable.ic_google_logo),
                        onClick = {
                            viewModel.onSignInClicked()
                            authResultLauncher.launch(signInRequestCode)
                        }
                    )
                    state.signInErrorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = it)
                    }
                }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Used libraries",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "AndroidX bundle",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Firebase",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Giphy",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Timber",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
