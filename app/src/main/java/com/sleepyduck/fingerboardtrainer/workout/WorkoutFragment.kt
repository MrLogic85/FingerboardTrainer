package com.sleepyduck.fingerboardtrainer.workout

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.sleepyduck.datamodel.Workout
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.asActivity
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.ListUIAdapterItem
import com.sleepyduck.workoutui.adapteritem.ItemWorkout
import com.sleepyduck.workoutui.adapteritem.ItemWorkoutRepeat
import com.sleepyduck.workoutui.adapteritem.ItemWorkoutSay
import com.sleepyduck.workoutui.replaceWhen
import com.sleepyduck.workoutui.setupForListUIAdapter
import com.sleepyduck.workoutui.toListUIAdapterItems
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_workout.*

class WorkoutFragment : Fragment() {

    private var workoutItems: List<ListUIAdapterItem> = listOf()
    private val adapter = ListUIAdapter()
    private lateinit var workout: Workout

    private val onPlayClickListener = View.OnClickListener {
        val repeat1 = workoutItems[0] as? ItemWorkoutRepeat
        val repeat2 = repeat1?.items?.get(0) as? ItemWorkoutRepeat
        val hangItem = repeat2?.items?.get(0) as? ItemWorkoutSay
        val pauseItem = repeat2?.items?.get(1) as? ItemWorkoutSay
        val handler = Handler()

        handler.postDelayed({
            hangItem?.selected = true
        }, 1000)

        for (delay in 1000L..10000L step 1000L) {
            handler.postDelayed({
                hangItem?.timeLeft = 10000L - delay
            }, delay)
        }

        handler.postDelayed({
            hangItem?.timeLeft = 10000L
            hangItem?.selected = false
            pauseItem?.selected = true
        }, 11000)

        for (delay in 11000L..20000L step 1000L) {
            handler.postDelayed({
                pauseItem?.timeLeft = 20000L - delay
            }, delay)
        }

        handler.postDelayed({
            pauseItem?.timeLeft = 10000L
            pauseItem?.selected = false
        }, 21000)
    }

    private val onItemClickListener = { itemWorkout: ItemWorkout ->
        val editWorkoutListener = { changedWorkoutElement: WorkoutElement ->
            val workoutData = workout.workoutData.replaceWhen(changedWorkoutElement) { it.id == changedWorkoutElement.id }
            updateWorkout(workout.copy(workoutData = workoutData))
        }

        asActivity {
            MaterialDialog(this).show {
                editWorkout(itemWorkout.workout, editWorkoutListener)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateWorkout(
            arguments?.getParcelable("workout") ?: Workout(title = "Error, failed to load workout")
        )

        recyclerView.setupForListUIAdapter()
        recyclerView.adapter = adapter

        asActivity {
            supportActionBar?.title = workout.title
        }

        view.rootView?.actionButton?.run {
            setImageResource(R.drawable.icon_play_white)
            setOnClickListener(onPlayClickListener)
        }
    }

    private fun updateWorkout(workout: Workout) {
        this.workout = workout
        workoutItems = workout.workoutData.toListUIAdapterItems(adapter, onItemClickListener)
        adapter.items = workoutItems
    }
}
