package com.sleepyduck.fingerboardtrainer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sleepyduck.datamodel.Workout

class WorkoutsViewModel : ViewModel() {
    val workouts = MutableLiveData<List<Workout>>()
}