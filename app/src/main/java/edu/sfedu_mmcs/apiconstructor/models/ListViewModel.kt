package edu.sfedu_mmcs.apiconstructor.models

import android.content.SharedPreferences
import android.util.Log
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

class ListViewModel(val reqRoute: String, val sp: SharedPreferences) : ViewModel(){
    val dataList = MutableLiveData<List<String>>()
    private val api = Api(
        sp.getString("saved_url", "http://10.0.2.2:8000").toString(),
        sp.getString("saved_spec", "http://10.0.2.2:8000/openapi.json").toString()
    )
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getData() {

        val request = Request.Builder()
            .url(api.getBaseApi() + reqRoute)
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

                    val listType = object : TypeToken<List<Map<String, String>>>() {}.type
                    val list = gson.fromJson<List<Map<String, String>>>(resp_text, listType)


                    dataList.postValue(
                        list.map {
                            it.entries.joinToString(", ")
                            { entry -> "\"${entry.key}\":${entry.value}" }
                        }
                    )
                }
            }
        })

    }
}