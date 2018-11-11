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
import com.google.firebase.Timestamp
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.fingerboardtrainer.data.*
import com.sleepyduck.fingerboardtrainer.viewmodel.FirebaseUserViewModel
import com.sleepyduck.fingerboardtrainer.viewmodel.WorkoutsViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_list_workouts.*
import java.util.*

class ListWorkoutsFragment : Fragment() {

    private val data = Database()
    private var firebaseUserViewModel: FirebaseUserViewModel? = null
    private var workoutsViewModel: WorkoutsViewModel? = null
    private val adapter = ListWorkoutsAdapter(listOf())

    private val defaultWorkouts = {
        listOf(
                Workout(
                        title = "Easy Workout",
                        description = "Shot quick workout",
                        icon = Icon.GIRL_EASY_CLIMB.id,
                        workout = WorkoutElement(
                                type = WorkoutElementType.REPEAT.id,
                                repeat = 3,
                                workouts = listOf(
                                        WorkoutElement(
                                                type = WorkoutElementType.REPEAT.id,
                                                repeat = 3,
                                                workouts = listOf(
                                                        WorkoutElement(
                                                                type = WorkoutElementType.SAY.id,
                                                                say = "Start",
                                                                timeMillis = 10000L),
                                                        WorkoutElement(
                                                                type = WorkoutElementType.SAY.id,
                                                                say = "Stop",
                                                                timeMillis = 10000L)
                                                )
                                        ),
                                        WorkoutElement(
                                                type = WorkoutElementType.PAUSE.id,
                                                timeMillis = 300000L
                                        )
                                )
                        )
                ),

                Workout(title = "Hard Workout",
                        description = "Long hangs with short breaks",
                        icon = Icon.GIRL_HARD_CLIMB.id,
                        workout = WorkoutElement(
                                type = WorkoutElementType.SAY.id,
                                say = "No")))
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
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        view.rootView?.addButton?.apply {
            show()
            setOnClickListener(onAddNewWorkoutClicked)
        }
    }

    override fun onDestroyView() {
        view?.rootView?.addButton?.hide()
        super.onDestroyView()
    }

    private fun initializeViewModel() = asActivity {
        firebaseUserViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
        workoutsViewModel = ViewModelProviders.of(this)[WorkoutsViewModel::class.java]

        workoutsViewModel?.workouts?.observe(this, Observer { workouts ->
            adapter.workouts = workouts
        })
    }

    private val onAddNewWorkoutClicked = View.OnClickListener {
        // TODO
    }
}

private fun Int.stampMinutesToSeconds(): Timestamp = (this * 60).stampSecondsToMillis()

private fun Int.stampSecondsToMillis(): Timestamp = Timestamp(Date(this * 1000L))