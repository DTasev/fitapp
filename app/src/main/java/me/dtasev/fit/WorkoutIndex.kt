package me.dtasev.fit

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_workout_index.*
import me.dtasev.fit.models.TodayModel
import me.dtasev.fit.util.Web
import org.json.JSONObject
import java.lang.ref.WeakReference
import android.widget.ArrayAdapter
import android.R.attr.data
import android.app.Activity


class WorkoutIndex : AppCompatActivity() {
    private lateinit var userAuthToken: String

    lateinit var model: TodayModel

    companion object {
        const val USER_AUTH_TOKEN = "user_auth_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_index)

        userAuthToken = intent.getStringExtra(USER_AUTH_TOKEN)

        val getToday = GetTodayTask(WeakReference(this), userAuthToken)
        getToday.execute()
    }

    fun showToday(response: JSONObject) {
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


    fun displayExercise(parent: AdapterView<*>, view: View, position: Int, id: Long) {
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

class GetTodayTask internal constructor(private val owner: WeakReference<WorkoutIndex>, private val token: String) : AsyncTask<Void, Void, JSONObject>() {
    private lateinit var web: Web
    override fun doInBackground(vararg params: Void): JSONObject? {
        web = Web()

        return try {
            web.Get("${owner.get()!!.getString(R.string.base_url)}/api/v1/today/", token)
        } catch (e: InterruptedException) {
            null
        }
    }

    override fun onPostExecute(success: JSONObject?) {
//            mAuthTask = null
//            showProgress(false)

        if (success != null) {
            owner.get()!!.model = TodayModel(success)
            owner.get()!!.showToday(success)
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
