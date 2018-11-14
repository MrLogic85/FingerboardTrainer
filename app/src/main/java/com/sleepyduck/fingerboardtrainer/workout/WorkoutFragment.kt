package com.sleepyduck.fingerboardtrainer.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.fingerboardtrainer.data.Workout
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.toListUIAdapterItems
import com.sleepyduck.listui.ListUIAdapter
import kotlinx.android.synthetic.main.fragment_workout.*

class WorkoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workout = arguments?.getParcelable<Workout>("workout")
        val adapterItems = workout?.workoutData?.let { workoutData: List<WorkoutElement> ->
            workoutData.toListUIAdapterItems(workoutData[0].workouts?.get(0)?.workouts?.get(0))
        }

        recyclerView.adapter = ListUIAdapter(recyclerView, adapterItems ?: listOf())

        asActivity {
            supportActionBar?.title = workout?.title
        }
    }
}