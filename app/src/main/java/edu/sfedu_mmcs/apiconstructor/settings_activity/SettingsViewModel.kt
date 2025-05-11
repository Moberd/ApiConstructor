package edu.sfedu_mmcs.apiconstructor.settings_activity

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.AuthInfo
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.SwaggerParseResult
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class SettingsViewModel(
    private val sp: SharedPreferences
):  ViewModel() {
    val authList = MutableLiveData<List<AuthInfo>>()
    private val api = Api(
        sp.getString("saved_url", "http://10.0.2.2:8000").toString(),
        sp.getString("saved_spec", "http://10.0.2.2:8000/openapi.json").toString()
    )
    private val client = OkHttpClient()

    fun getSecurityTypes() {
        val specRoute = api.getOpenApi()
        val request = Request.Builder()
            .url(specRoute)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val specContent = response.body!!.string()

                val parser = OpenAPIV3Parser()
                val result: SwaggerParseResult = parser.readContents(
                    specContent,
                    null,
                    null
                )
                val openAPI = result.openAPI
                val secShemas = openAPI.components.securitySchemes
                val authRes = ArrayList<AuthInfo>()
                for (schema in secShemas.keys) {
                    Log.d("settings", secShemas[schema].toString())
                    if ((secShemas[schema]?.type.toString().lowercase()) == "apikey" && (secShemas[schema]?.`in`.toString().lowercase()) == "header") {
                        authRes.add(AuthInfo(schema, secShemas[schema]?.type.toString(), ""))
                    }
                }
                authList.postValue(authRes)
            }
        })
    }
}