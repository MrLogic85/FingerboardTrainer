package com.sleepyduck.fingerboardtrainer.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class FirebaseUserViewModel : ViewModel() {
    var user: FirebaseUser? = null
}