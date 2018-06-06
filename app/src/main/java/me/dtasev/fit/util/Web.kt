package me.dtasev.fit.util

import android.app.DownloadManager
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.util.Base64
import android.os.StrictMode
import me.dtasev.fit.R.id.password
import me.dtasev.fit.models.ExerciseSet
import me.dtasev.fit.models.WorkoutExercise
import org.json.JSONArray
import org.json.JSONObject


class Web {
    companion object {
        var USER_AUTH_TOKEN = ""
    }

    fun Get(url: String, token: String): JSONObject {
        val mUrl = URL(url)
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
                return JSONObject(response.toString())
            }
            return JSONObject("")
        }
    }

    fun POSTNewSet(url: String, token: String, workoutExercise: WorkoutExercise, newSet: ExerciseSet): JSONObject {
        val data = """
            {
                "workoutexercise_id":${workoutExercise.id},
                "new_set":{
                    "kgs":${newSet.kgs},
                    "reps":${newSet.reps}
                }
            }
        """.trimIndent()

        return POST(url, token, data, 201)
    }

    fun POST(url: String, token: String, data: String, expectedStatusCode: Int): JSONObject {
        val mUrl = URL(url)
        with(mUrl.openConnection() as HttpURLConnection) {
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-type", "application/json")
            requestMethod = "POST"
            setRequestProperty("Authorization", "Token $token")

            val wr = OutputStreamWriter(outputStream)
            wr.write(data)
            wr.flush()
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
                return JSONObject(response.toString())
            }
            return JSONObject("")
        }
    }

    fun getUserAuthToken(url: String, userName: String, password: String): JSONObject {
        val reqParam = "{\"username\":\"$userName\",\"password\":\"$password\"}"
        val mUrl = URL(url)
        with(mUrl.openConnection() as HttpURLConnection) {
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-type", "application/json")
            requestMethod = "POST"
//            setRequestProperty("Authentication",
//                    Base64.encodeToString("Basic $userName:$password".toByteArray(), Base64.DEFAULT))

            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()
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
                return JSONObject(response.toString())
            }
            return JSONObject("")
        }

//        val urlConnection = url.openConnection() as HttpURLConnection
//        urlConnection.requestMethod = "POST"
//
//        add the POST data for token retrieval
//        val wr = OutputStreamWriter(urlConnection.outputStream)
//        wr.write(reqParam)
//        wr.flush()
//
//        try {
//            val `in` = BufferedInputStream(urlConnection.inputStream)
//            BufferedReader(InputStreamReader(`in`)).use {
//                val response = StringBuffer()
//
//                var inputLine = it.readLine()
//                while (inputLine != null) {
//                    response.append(inputLine)
//                    inputLine = it.readLine()
//                }
//                it.close()
//                println("Response : $response")
//            }
//        } finally {
//            urlConnection.disconnect()
//        }
    }
}