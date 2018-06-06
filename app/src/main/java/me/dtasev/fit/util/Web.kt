package me.dtasev.fit.util

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import me.dtasev.fit.models.ExerciseSet
import me.dtasev.fit.models.WorkoutExercise
import org.json.JSONObject
import java.lang.ref.WeakReference


class Web<T> constructor(private val owner: WeakReference<T>, private val token: String?) {
    var inProgress = false

    companion object {
        var USER_AUTH_TOKEN = ""
    }


    fun get(url: String): JSONObject {
        val mUrl = URL(url)
        inProgress = true
        with(mUrl.openConnection() as HttpURLConnection) {
            setRequestProperty("Accept", "application/json")
            requestMethod = "GET"
//            setRequestProperty("Authorization", Base64.encodeToString("Token $token".toByteArray(), Base64.DEFAULT))
            setRequestProperty("Authorization", "Token $token")

            println("URL : $mUrl")
            println("Response Code : $responseCode")
            println(responseMessage)
            val response: StringBuffer
            if (responseCode == 200) {
                response = StringBuffer()
                val `in` = BufferedReader(InputStreamReader(inputStream))
                `in`.use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    println("Response : $response")
                }
                inProgress = false
                return JSONObject(response.toString())
            }
            inProgress = false
            return JSONObject("")
        }
    }

//    fun postNewSet(url: String, workoutExercise: WorkoutExercise, newSet: ExerciseSet): JSONObject {
//        val data = """
//            {
//                "workoutexercise_id":${workoutExercise.id},
//                "new_set":{
//                    "kgs":${newSet.kgs},
//                    "reps":${newSet.reps}
//                }
//            }
//        """.trimIndent()
//
//        return
//    }

    fun post(url: String, data: String?, expectedStatusCode: Int): JSONObject {
        val mUrl = URL(url)
        inProgress = true
        with(mUrl.openConnection() as HttpURLConnection) {
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-type", "application/json")
            requestMethod = "POST"
            if (token != null) {
                setRequestProperty("Authorization", "Token $token")
            }

            val wr = OutputStreamWriter(outputStream)
            if (data != null) {
                wr.write(data)
                wr.flush()
            }
            println("URL : $mUrl")
            println("Response Code : $responseCode")
            println(responseMessage)
            val response: StringBuffer
            if (responseCode == expectedStatusCode) {
                response = StringBuffer()
                val `in` = BufferedReader(InputStreamReader(inputStream))
                `in`.use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    println("Response : $response")
                }
                inProgress = false
                return JSONObject(response.toString())
            }
            inProgress = false
            return JSONObject("")
        }
    }

    fun deleteSet(url: String,
                  onSuccess: ((JSONObject) -> Unit)?,
                  onFailure: (() -> Unit)?): AsyncTask<Void, Void, JSONObject> {

        val task = WebTask({ this.post(url, "", 200) }, onSuccess, onFailure)
        task.execute()
        return task
    }

    fun getToday(url: String, onSuccess: ((JSONObject) -> Unit)?,
                 onFailure: (() -> Unit)?): AsyncTask<Void, Void, JSONObject> {
        val task = WebTask({ get(url) }, onSuccess, onFailure)
        task.execute()
        return task
    }

    fun login(url: String, userName: String, password: String, onSuccess: ((JSONObject) -> Unit)?, onFailure: (() -> Unit)?): AsyncTask<Void, Void, JSONObject> {
        val reqParam = """{"username":"$userName","password":"$password"}"""
        val task = WebTask({ post(url, reqParam, 200) }, onSuccess, onFailure)
        task.execute()
        return task
    }

    fun addNewSet(url: String,
                  workoutExercise: WorkoutExercise,
                  newSet: ExerciseSet,
                  onSuccess: ((JSONObject) -> Unit)?,
                  onFailure: (() -> Unit)?): WebTask {
        val data = """
            {
                "workoutexercise_id":${workoutExercise.id},
                "new_set":{
                    "kgs":${newSet.kgs},
                    "reps":${newSet.reps}
                }
            }
        """.trimIndent()

        val task = WebTask({ this.post(url, data, 201) }, onSuccess, onFailure)
        task.execute()
        return task
    }
}

class WebTask internal constructor(private val requestCall: (() -> JSONObject),
                                   private val onSuccess: ((JSONObject) -> Unit)?,
                                   private val onFailure: (() -> Unit)?) : AsyncTask<Void, Void, JSONObject>() {

    override fun doInBackground(vararg params: Void): JSONObject? {
        return try {
            requestCall.invoke()
        } catch (e: Exception) {
            null
        }
    }

    override fun onPostExecute(success: JSONObject?) {
//            showProgress(false)

        if (success != null) {
            onSuccess?.invoke(success)
        } else {
            onFailure?.invoke()
        }
    }

    override fun onCancelled() {
//            mAuthTask = null
//            showProgress(false)
    }
}