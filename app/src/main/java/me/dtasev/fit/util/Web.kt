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


class Web {


    fun POST(userName: String, password: String) {
        val reqParam = "{\"username\":\"$userName\",\"password\":\"$password\"}"
        val url = URL("http://10.0.2.2:5544/api/v1/auth-login-token/")
        with(url.openConnection() as HttpURLConnection) {
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-type", "application/json")
            requestMethod = "POST"
//            setRequestProperty("Authentication",
//                    Base64.encodeToString("Basic $userName:$password".toByteArray(), Base64.DEFAULT))

            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()
            println("URL : $url")
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
            }
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