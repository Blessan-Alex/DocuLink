package com.example.doculink

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Http {

    companion object {
        // USB reverse example (adb reverse tcp:8080 tcp:8000)
        private const val BASE_URL = "http://127.0.0.1:8080/"

        private val gson = Gson()

        fun sendQuery(
            query: String,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            Thread {
                try {
                    val url = URL("${BASE_URL}api/query")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        // ⬇️ bigger timeouts so we can wait for your ~38s processing
                        connectTimeout = 15_000      // time to establish TCP
                        readTimeout = 120_000        // time to wait for response bytes
                        doOutput = true
                        doInput = true
                        // Optional: avoid buffering full request in memory for large payloads
                        // setChunkedStreamingMode(0)
                    }

                    val jsonRequest = gson.toJson(QueryRequest(query = query))
                    OutputStreamWriter(conn.outputStream).use { it.write(jsonRequest) }

                    val code = conn.responseCode
                    if (code == HttpURLConnection.HTTP_OK) {
                        BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                            val text = reader.readText()
                            val obj = gson.fromJson(text, QueryResponse::class.java)
                            onSuccess(obj.response)
                        }
                    } else {
                        // Read error stream for useful message if available
                        val msg = try {
                            BufferedReader(InputStreamReader(conn.errorStream)).use { it.readText() }
                        } catch (_: Exception) { "HTTP $code" }
                        onError("Server error: $msg")
                    }
                } catch (e: Exception) {
                    onError("Error: ${e.message}")
                }
            }.start()
        }

        fun checkHealth(callback: (Boolean) -> Unit) {
            Thread {
                val healthy = try {
                    val url = URL("${BASE_URL}api/health")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 10_000
                        readTimeout = 10_000
                    }
                    conn.responseCode == HttpURLConnection.HTTP_OK
                } catch (_: Exception) { false }
                callback(healthy)
            }.start()
        }
    }
}
