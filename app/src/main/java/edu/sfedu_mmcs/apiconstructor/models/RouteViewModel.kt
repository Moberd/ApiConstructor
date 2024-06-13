package edu.sfedu_mmcs.apiconstructor.models

import android.telecom.Call
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class RouteViewModel:  ViewModel() {
    val routeList = MutableLiveData<List<RouteInfo>>()
    private val api = Api()
    private val gson = Gson()
    private val client = OkHttpClient()


    fun getRoutes() {

        val request = Request.Builder()
            .url(api.getApiDescriptionRoute())
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        throw IOException("Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}")
                    }
                    val resp_text = response.body!!.string()
                    Log.d("Response", resp_text)
                    routeList.postValue(
                        gson.fromJson(
                            resp_text, object : TypeToken<List<RouteInfo>>() {}.type
                        )
                    )
                }
            }
        })

    }
}