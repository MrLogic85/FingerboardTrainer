package com.sleepyduck.fingerboardtrainer.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sleepyduck.fingerboardtrainer.MainActivity.Companion.REQUEST_CODE_SIGN_ID
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity


class LogInFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asActivity {
            val account = GoogleSignIn.getLastSignedInAccount(this)

            when (account) {
                null -> {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val signInClient = GoogleSignIn.getClient(this, gso)
                    val signInIntent = signInClient.signInIntent
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_ID)
                }

                else -> loggedInComplete()
            }
        }
    }
}