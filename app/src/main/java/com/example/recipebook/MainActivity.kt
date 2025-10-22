package com.example.recipebook

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        Toast.makeText(this, "${Firebase.auth.currentUser?.email}",Toast.LENGTH_LONG).show()

        findViewById<Button>(R.id.logout).setOnClickListener {
            lifecycleScope.launch {
                val logoutrequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(logoutrequest)
                Firebase.auth.signOut()
            }
        }

        /*
        findViewById<Button>(R.id.signup).setOnClickListener {
            lifecycleScope.launch {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity,
                )
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    val data = Firebase.auth.currentUser?.linkWithCredential(firebaseCredential)?.await()

                    Log.i("Success", "${data} ${data?.user?.displayName} ${data?.user?.email} ${data?.user?.uid}")
                } else {
                    Log.e("Errors", "error")
                }
            }
        }

         */

        findViewById<Button>(R.id.signin).setOnClickListener {
            lifecycleScope.launch {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@MainActivity,
                )
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    val data = Firebase.auth.signInWithCredential(firebaseCredential).await()

                    Log.i("Success", "${data.user?.displayName} ${data.user?.email} ${data.user?.uid}")
                } else {
                    Log.e("Errors", "error")
                }
            }
        }

    }
}