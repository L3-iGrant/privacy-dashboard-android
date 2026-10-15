package com.github.privacydashboardandroid

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Stub of the backend of the host app.
 *
 * In a real app, the host backend knows the logged-in user from its own
 * session. It calls `POST /v3/service/individual/token` of the iGrant.io api
 * with its server-only API key, and gives the `access_token` to the app. The
 * app never has the API key.
 *
 * This sample sends `POST <tokenUrl>` and reads `access_token` from the JSON
 * response. The token URL is an input field of the sample. There is no secret
 * in the sample source.
 */
class SampleHostBackend(private val tokenUrl: String) {

    private val client = OkHttpClient()

    fun privacyDashboardToken(callback: (token: String?, error: Throwable?) -> Unit) {
        val request = Request.Builder()
            .url(tokenUrl)
            .post("{}".toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null, IOException("Host backend answered ${it.code}"))
                        return
                    }
                    try {
                        val token = JSONObject(it.body?.string() ?: "").getString("access_token")
                        callback(token, null)
                    } catch (e: Exception) {
                        callback(null, e)
                    }
                }
            }
        })
    }
}
