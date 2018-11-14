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
import com.sleepyduck.fingerboardtrainer.data.WorkoutElementType
import com.sleepyduck.fingerboardtrainer.data.toWorkoutElementType
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutPause
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutRepeat
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutSay
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.ListUIAdapterItem
import kotlinx.android.synthetic.main.fragment_workout.*

class WorkoutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workout = arguments?.getParcelable<Workout>("workout")
        val adapterItems = workout?.workoutData?.let { workoutData ->
            mapWorkoutToAdapterItems(workoutData)
        }

        recyclerView.adapter = ListUIAdapter(adapterItems ?: listOf())

        asActivity {
            supportActionBar?.title = workout?.title
        }
    }

    private fun mapWorkoutToAdapterItems(workoutElements: List<WorkoutElement>): List<ListUIAdapterItem> =
        workoutElements.map { element ->
            when (element.type.toWorkoutElementType()) {
                WorkoutElementType.REPEAT -> ItemWorkoutRepeat(
                    identifier = element.hashCode().toLong(),
                    name = element.name ?: "",
                    count = element.repeat ?: 0,
                    items = mapWorkoutToAdapterItems(element.workouts ?: listOf())
                )

                WorkoutElementType.SAY -> ItemWorkoutSay(
                    identifier = element.hashCode().toLong(),
                    name = element.name ?: "",
                    say = element.say ?: ""
                )

                WorkoutElementType.PAUSE -> ItemWorkoutPause(
                    identifier = element.hashCode().toLong(),
                    name = element.name ?: ""
                )
            }
        }
}