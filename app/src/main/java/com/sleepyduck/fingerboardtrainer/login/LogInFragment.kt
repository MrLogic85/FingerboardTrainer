package com.sleepyduck.fingerboardtrainer.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sleepyduck.fingerboardtrainer.MainActivity.Companion.REQUEST_CODE_SIGN_ID
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.fingerboardtrainer.viewmodel.FirebaseUserViewModel

class LogInFragment : Fragment() {

    private var firebaseUserViewModel: FirebaseUserViewModel? = null
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        asActivity {
            firebaseUserViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
            logInToGoogle()
        }
    }

    private fun logInToGoogle() {
        asActivity {
            val account = GoogleSignIn.getLastSignedInAccount(this)

            when (account) {
                null -> {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .build()
                    val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
                    requestHandlers[REQUEST_CODE_SIGN_ID] = ::handleSignIn
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_ID)
                }

                else -> logInToFirebase(account)
            }
        }
    }

    private fun handleSignIn(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)?.account
            when (account) {
                null -> logInFailed()
                else -> logInToGoogle()
            }
        } catch (e: ApiException) {
            logInFailed()
        }
    }

    private fun logInToFirebase(account: GoogleSignInAccount) {
        when (auth.currentUser) {
            null -> {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                        .addOnSuccessListener { authRes ->
                            firebaseUserViewModel?.user = authRes.user
                            loggedInComplete()
                        }
                        .addOnFailureListener {
                            logInFailed()
                        }
                        .addOnCanceledListener {
                            logInFailed()
                        }
            }

            else -> {
                firebaseUserViewModel?.user = auth.currentUser
                loggedInComplete()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_log_in, container, false)

    private fun loggedInComplete() {
        findNavController().navigate(R.id.navActionLoggedIn)
    }

    private fun logInFailed() {
        TODO("Handle log in failure")
    }
}