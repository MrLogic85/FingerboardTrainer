package com.sleepyduck.fingerboardtrainer.listworkouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.fingerboardtrainer.data.Database
import com.sleepyduck.fingerboardtrainer.data.Workout
import com.sleepyduck.fingerboardtrainer.data.createDefaultWorkouts
import com.sleepyduck.fingerboardtrainer.viewmodel.FirebaseUserViewModel
import com.sleepyduck.fingerboardtrainer.viewmodel.WorkoutsViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_list_workouts.*

class ListWorkoutsFragment : Fragment() {

    private val workoutClickListener = { workout: Workout ->
        val bundle = bundleOf("workout" to workout)
        findNavController().navigate(R.id.actionWorkout, bundle)
    }

    private val onAddNewWorkoutClicked = View.OnClickListener {
        // TODO
    }

    private val data = Database()
    private var firebaseUserViewModel: FirebaseUserViewModel? = null
    private var workoutsViewModel: WorkoutsViewModel? = null
    private val adapter = ListWorkoutsAdapter(listOf(), workoutClickListener)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeViewModel()

        val user = firebaseUserViewModel?.user
        when (user) {
            null -> findNavController().navigate(R.id.logInFragment)
            else -> data.getOrCreateWorkoutData(user, ::createDefaultWorkouts) { workouts ->
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

        view.rootView?.actionButton?.run {
            setImageResource(R.drawable.icon_add_white)
            setOnClickListener(onAddNewWorkoutClicked)
        }
    }

    private fun initializeViewModel() = asActivity {
        firebaseUserViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
        workoutsViewModel = ViewModelProviders.of(this)[WorkoutsViewModel::class.java]

        workoutsViewModel?.workouts?.observe(this, Observer { workouts ->
            adapter.workouts = workouts
        })
    }
}