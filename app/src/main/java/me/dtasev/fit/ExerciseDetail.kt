package me.dtasev.fit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_exercise_detail.*
import me.dtasev.fit.R.id.*
import me.dtasev.fit.models.ExerciseSet
import me.dtasev.fit.models.WorkoutExercise
import me.dtasev.fit.util.Web
import org.json.JSONObject
import java.lang.ref.WeakReference

class ExerciseDetail : AppCompatActivity() {
    companion object {
        const val EXERCISE_DETAILS = "exercise_details"
    }

    val web = Web<ExerciseDetail>(WeakReference(this), Web.USER_AUTH_TOKEN)
    lateinit var workoutExercise: WorkoutExercise

    var setsList = mutableListOf<String>()
    //    var newSets = mutableListOf<ExerciseSet>()
    var selectedLongPressItem: Int = 0

    val padding = "                   "

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

            registerForContextMenu(exerciseSetsContainer)

        }
        updateSetsListView(true)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        // We need to subtract -1, as the ListView has a heading added for prettier look
        // subtracting gives us the actual position in the workoutExercise.sets model
        selectedLongPressItem = (menuInfo as AdapterView.AdapterContextMenuInfo).position - 1
        menuInflater.inflate(R.menu.exercise_set_options, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.option1 -> {
                deleteSet(workoutExercise.sets[selectedLongPressItem])
                return true
            }
            R.id.option2 -> {
                editSet(workoutExercise.sets[selectedLongPressItem])
                return true
            }
            else -> return false
        }
    }

    private fun deleteSet(exerciseSet: ExerciseSet) {
        web.deleteSet("${getString(R.string.base_url)}/api/v1/sets/delete/${exerciseSet.id}", {
            workoutExercise.sets.removeAt(workoutExercise.sets.indexOf(exerciseSet))
            updateSetsListView(true)
        }, null)
    }

    private fun editSet(exerciseSet: ExerciseSet) {

    }

    fun addSet(view: View) {
        val newSet = ExerciseSet(-1, exerciseKgs.text.toString().toFloat(), exerciseReps.text.toString().toInt())
        web.addNewSet("${getString(R.string.base_url)}/api/v1/sets/add/", workoutExercise, newSet,
                {
                    this.addNewSetToListView(ExerciseSet((it["new_set"] as JSONObject)["id"].toString().toInt(), newSet.kgs, newSet.reps))
                }, null)
    }

    private fun addNewSetToListView(newSet: ExerciseSet) {
        workoutExercise.sets.add(newSet)
        setsList.add("$padding${setsList.size}$padding${newSet.kgs}$padding${newSet.reps}")

        updateSetsListView()
    }

    /**
     * @param full Perform a full recalculation of the setsList based on the current model
     */
    private fun updateSetsListView(full: Boolean = false) {
        if (full) {
            var id = 1
            setsList = workoutExercise.sets.map { set ->
                "$padding${id++}$padding${set.kgs}$padding${set.reps}"
            }.toMutableList()

            // add 'heading' to list view items, as the first list view item
            setsList.add(0, "$padding#${padding}Kgs${padding}Reps")

        }
        val setsAdapter = ArrayAdapter<String>(this, R.layout.workout_index_row, setsList)
        exerciseSetsContainer.adapter = setsAdapter
    }
}
