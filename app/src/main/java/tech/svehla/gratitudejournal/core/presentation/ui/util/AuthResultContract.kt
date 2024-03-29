package tech.svehla.gratitudejournal.core.presentation.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

class AuthResultContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent =
        getGoogleSignInClient(context).signInIntent.putExtra("input", input)

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount> {
        val result = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return when (resultCode) {
            Activity.RESULT_OK -> result
            else -> throw RuntimeException("Failed to sign in")
        }
    }
}