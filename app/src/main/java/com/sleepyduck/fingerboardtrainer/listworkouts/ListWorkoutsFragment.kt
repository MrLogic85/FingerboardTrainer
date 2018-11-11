package com.sleepyduck.fingerboardtrainer.listworkouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.fingerboardtrainer.data.Database
import com.sleepyduck.fingerboardtrainer.data.Icon
import com.sleepyduck.fingerboardtrainer.data.Workout
import com.sleepyduck.fingerboardtrainer.viewmodel.FirebaseUserViewModel
import com.sleepyduck.fingerboardtrainer.viewmodel.WorkoutsViewModel
import kotlinx.android.synthetic.main.fragment_list_workouts.*

class ListWorkoutsFragment : Fragment() {

    private val data = Database()
    private var firebaseUserViewModel: FirebaseUserViewModel? = null
    private var workoutsViewModel: WorkoutsViewModel? = null
    private val adapter = ListWorkoutsAdapter(listOf())

    private val defaultWorkouts = {
        listOf(
                Workout(title = "Easy Workout",
                        description = "Shot quick workout",
                        icon = Icon.GIRL_EASY_CLIMB.id),

                Workout(title = "Hard Workout",
                        description = "Long hangs with short breaks",
                        icon = Icon.GIRL_HARD_CLIMB.id)
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeViewModel()

        val user = firebaseUserViewModel?.user
        when (user) {
            null -> findNavController().navigate(R.id.logInFragment)
            else -> data.getOrCreateWorkoutData(user, defaultWorkouts) { workouts ->
                workoutsViewModel?.workouts?.value = workouts
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_list_workouts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
    }

    private fun initializeViewModel() = asActivity {
        firebaseUserViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
        workoutsViewModel = ViewModelProviders.of(this)[WorkoutsViewModel::class.java]

        workoutsViewModel?.workouts?.observe(this, Observer { workouts ->
            adapter.workouts = workouts
        })
    }
}
