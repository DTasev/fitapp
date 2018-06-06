package me.dtasev.fit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_workout_index.*
import me.dtasev.fit.models.TodayModel
import me.dtasev.fit.util.Web
import org.json.JSONObject
import java.lang.ref.WeakReference


class WorkoutIndex : AppCompatActivity() {
    private lateinit var userAuthToken: String

    lateinit var model: TodayModel
    val web = Web<WorkoutIndex>(WeakReference(this), Web.USER_AUTH_TOKEN)

    companion object {
        const val USER_AUTH_TOKEN = "user_auth_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_index)

        userAuthToken = intent.getStringExtra(USER_AUTH_TOKEN)

        web.getToday("${getString(R.string.base_url)}/api/v1/today/", {
            model = TodayModel(it)
            showToday(it)
        }, null)
    }

    private fun showToday(response: JSONObject) {
        print(response)
        print(model.workout.date)

        val primary = model.primaryExercisesNamesList()
        // Create ArrayAdapter using the planet list.
        val primaryListAdapter = ArrayAdapter<String>(this, R.layout.workout_index_row, primary)
        today_primary_container.adapter = primaryListAdapter
        today_primary_container.onItemClickListener = AdapterView.OnItemClickListener(::displayExercise)

        val secondary = model.secondaryExercisesNamesList()
        val secondaryListAdapter = ArrayAdapter<String>(this, R.layout.workout_index_row, secondary)
        today_secondary_container.adapter = secondaryListAdapter
        today_secondary_container.onItemClickListener = AdapterView.OnItemClickListener(::displayExercise)
    }


    private fun displayExercise(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val exerciseDetail = Intent(this, ExerciseDetail::class.java)
        exerciseDetail.putExtra(ExerciseDetail.EXERCISE_DETAILS, model.workout.primaryExercises[position])
        exerciseDetail.putExtra(ExerciseDetail.EXERCISE_DETAILS, model.workout.primaryExercises[position])
        startActivity(exerciseDetail)
//        startActivityForResult(exerciseDetail, 1)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                val strEditText = data!!.getStringExtra("editTextValue")
//            }
//        }
//    }
}
