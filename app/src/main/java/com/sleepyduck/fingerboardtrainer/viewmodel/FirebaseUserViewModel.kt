package com.sleepyduck.fingerboardtrainer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class FirebaseUserViewModel : ViewModel() {
    val user = MutableLiveData<FirebaseUser>()
}