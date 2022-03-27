package tech.svehla.gratitudejournal.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.currentUser != null) {
            Text(
                text = "Logged in as ${state.currentUser!!.email}",
                style = MaterialTheme.typography.h6
            )
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.signOut()
            }) {
                Text(text = "Sign out")
            }
        } else {
            SignInButton(
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
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = it)
            }
        }
    }
}
