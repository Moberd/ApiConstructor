package edu.sfedu_mmcs.apiconstructor.form_activity

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import edu.sfedu_mmcs.apiconstructor.utils.AnyTypeAdapter
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo
import edu.sfedu_mmcs.apiconstructor.utils.UrlBuilder
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class FormViewModel(private val routeInfo: RouteInfo, val sp: SharedPreferences) : ViewModel() {

    val responseRes = MutableLiveData<String>()

    private val api = Api(
        sp.getString("saved_url", "http://10.0.2.2:8000").toString(),
        sp.getString("saved_spec", "http://10.0.2.2:8000/openapi.json").toString()
    )
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun sendData(fields: Map<String, String>, content: Map<String, String>) {
        val myGson = GsonBuilder().registerTypeAdapter(Any::class.java, AnyTypeAdapter()).create()

        val url = UrlBuilder.buildUrl(api.getBaseApi(), routeInfo.route, fields)
        val body = myGson.toJson(content).toRequestBody(JSON)

        val headersBuilder = Headers.Builder()
        headersBuilder.add("Content-Type", "application/json")
        for (name in routeInfo.security) {
            headersBuilder.add(name, sp.getString(name, "")!!)
        }
        val request = Request.Builder()
            .url(url)
            .method(routeInfo.method, if (routeInfo.method.uppercase() == "GET") null else body)
            .headers(headersBuilder.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                responseRes.postValue(e.message)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        responseRes.postValue("${response.code} ${response.message}" + routeInfo.responses.getOrDefault(response.code.toString(), routeInfo.responses["default"]))
                        throw IOException("Error sending request: ${response.code} ${response.message}")
                    }
                    val resp_text = response.body!!.string()
                    responseRes.postValue(resp_text)
                }
            }
        })
    }
}