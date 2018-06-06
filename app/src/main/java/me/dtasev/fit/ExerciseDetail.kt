package me.dtasev.fit

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TableRow
import kotlinx.android.synthetic.main.activity_exercise_detail.*
import kotlinx.android.synthetic.main.exercise_detail_row.view.*
import me.dtasev.fit.R.id.*
import me.dtasev.fit.models.ExerciseSet
import me.dtasev.fit.models.TodayModel
import me.dtasev.fit.models.WorkoutExercise
import me.dtasev.fit.util.Web
import org.json.JSONObject
import java.lang.ref.WeakReference

class ExerciseDetail : AppCompatActivity() {
    companion object {
        const val EXERCISE_DETAILS = "exercise_details"
    }

    lateinit var workoutExercise: WorkoutExercise

    lateinit var setsList: MutableList<String>
    var newSets = mutableListOf<ExerciseSet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)
        workoutExercise = intent.getSerializableExtra(EXERCISE_DETAILS) as WorkoutExercise
        println(workoutExercise)
        exerciseName.text = workoutExercise.exercise.name
//        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)

        if (workoutExercise.sets.isNotEmpty()) {
            val lastSet = workoutExercise.sets.last()
            exerciseKgs.setText(lastSet.kgs.toString())
            exerciseReps.setText(lastSet.reps.toString())

//            workoutExercise.sets.forEachIndexed { i, set ->
//                val exerciseSetRow = LayoutInflater.from(this).inflate(R.layout.exercise_detail_row, null) as TableRow
//                exerciseSetRow.set_id.text = i.toString()
//                exerciseSetRow.set_kgs.text = set.kgs.toString()
//                exerciseSetRow.set_reps.text = set.reps.toString()
//                exerciseTable.addView(exerciseSetRow)
//            }

            var id = 1
            setsList = workoutExercise.sets.map { set ->
                "${id++}            ${set.kgs}            ${set.reps}"
            }.toMutableList()

            val setsAdapter = ArrayAdapter<String>(this, R.layout.workout_index_row, setsList)
            exerciseSetsContainer.adapter = setsAdapter
        }
    }

    fun addSet(view: View) {
        val newSet = ExerciseSet(-1, exerciseKgs.text.toString().toFloat(), exerciseReps.text.toString().toInt())
        val task = AddSetTask(WeakReference(this), Web.USER_AUTH_TOKEN, workoutExercise, newSet)
        task.execute()
    }
}

class AddSetTask internal constructor(private val owner: WeakReference<ExerciseDetail>,
                                      private val token: String,
                                      private val workoutExercise: WorkoutExercise,
                                      private val newSet: ExerciseSet) : AsyncTask<Void, Void, JSONObject>() {
    private lateinit var web: Web
    override fun doInBackground(vararg params: Void): JSONObject? {
        web = Web()

        return try {
            web.POSTNewSet("${owner.get()!!.getString(R.string.base_url)}/api/v1/sets/add/", token, workoutExercise, newSet)
        } catch (e: InterruptedException) {
            null
        }
    }

    override fun onPostExecute(success: JSONObject?) {
//            showProgress(false)

        if (success != null) {
            val o = owner.get()!!
            // add locally to owner
            o.setsList.add("${o.workoutExercise.sets.last().id + 1}            ${newSet.kgs}            ${newSet.reps}")
            o.newSets.add(ExerciseSet((success["new_set"] as JSONObject)["id"].toString().toInt(), newSet.kgs, newSet.reps))
        } else {
//                password.error = getString(R.string.error_incorrect_auth)
//                password.requestFocus()
        }
    }

    override fun onCancelled() {
//            mAuthTask = null
//            showProgress(false)
    }
}