package edu.sfedu_mmcs.apiconstructor.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.sfedu_mmcs.apiconstructor.utils.AnyTypeAdapter
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.replacePlaceholders
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class FormViewModel(val reqRoute: String, val method: String) : ViewModel() {



    val responseRes = MutableLiveData<String>()
    private val api = Api()
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val gson = Gson()

    fun sendData(data: Map<String, Any>) {

         val myGson = GsonBuilder().registerTypeAdapter(Any::class.java, AnyTypeAdapter()).create()

        val newReqRoute = replacePlaceholders(reqRoute, data)
        val body: RequestBody = myGson.toJson(data).toRequestBody(JSON)
        val request = Request.Builder()
            .url(api.getBaseApi() + newReqRoute)
            .method(method, body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        throw IOException("Error sending request:" +
                                " ${response.code} ${response.message}")
                    }
                    val resp_text = response.body!!.string()
                    Log.d("Response", resp_text)
                    responseRes.postValue(resp_text)
                }
            }
        })

    }
}