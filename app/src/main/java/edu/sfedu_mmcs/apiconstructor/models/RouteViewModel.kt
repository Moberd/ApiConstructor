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
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.SwaggerParseResult
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


    fun getSchemaInfo(schema: Schema<*>, openAPI: OpenAPI): List<String> {
        val result = ArrayList<String>()
        if (schema.`$ref` != null) {
            Log.d("Path parameter","ref: ${schema.`$ref`}")
            result.addAll(resolveSchemaRef(schema.`$ref`, openAPI))
        } else {
            schema.properties?.forEach { (name, propSchema) ->
                result.add(name)
            }
        }
        return result
    }

    // Function to resolve and print information about a schema reference from openAPI
    fun resolveSchemaRef(ref: String, openAPI: OpenAPI): List<String> {
        val refPath = ref.replace("#/components/schemas/", "")
        val schema = openAPI.components?.schemas?.get(refPath)
        val result = ArrayList<String>()
        if (schema != null) {
            Log.d("Path parameter","        Resolved schema at: /components/schemas/$refPath")
            Log.d("Path parameter","        Type: ${schema.type}")
            schema.properties?.forEach { (name, propSchema) ->
                result.add(name)
            }
        } else {
            Log.d("Path parameter","        Could not resolve schema for $ref")
        }
        return result
    }

    fun getRoutes() {

        val request = Request.Builder()
            .url(api.getOpenApi())
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
                    val specContent = response.body!!.string()
                    val parser = OpenAPIV3Parser()
                    val result: SwaggerParseResult = parser.readContents(
                        specContent,
                        null,
                        null
                    )
                    val openAPI = result.openAPI
                    val routesRes = ArrayList<RouteInfo>()
                    for ((pathKey, pathItem) in openAPI.paths) {
                        Log.d("Path parameter", pathKey)
                        pathItem.readOperationsMap().forEach { (httpMethod, operation) ->
                            val fieldsArr = ArrayList<String>()
                            operation.parameters?.forEach { parameter ->
                                fieldsArr.add(parameter.name)
                            }
                            operation.requestBody?.let { requestBody ->
                                requestBody.content?.forEach { (mediaType, mediaTypeObject) ->
                                    getSchemaInfo(mediaTypeObject.schema, openAPI)
                                }
                            }
//                            operation.responses?.let { responses ->
//                                println("    Responses:")
//                                responses.forEach { (statusCode, response: ApiResponse) ->
//                                    println("      Status Code: $statusCode")
//                                    println("      Description: ${response.description}")
//                                    response.content?.forEach { (mediaType, mediaTypeObject) ->
//                                        println("        Media Type: $mediaType")
//                                        println("        Schema: ${mediaTypeObject.schema?.type}")
//                                    }
//                                }
//                            }
                            routesRes.add(
                                RouteInfo(
                                    pathKey,
                                    httpMethod.name,
                                    if (fieldsArr.size > 0) "form" else "list",
                                    fieldsArr
                                )
                            )
                        }
                    }
                    routeList.postValue(routesRes)
//                    routeList.postValue(
//                        gson.fromJson(
//                            specContent, object : TypeToken<List<RouteInfo>>() {}.type
//                        )
//                    )
                }
            }
        })

    }
}