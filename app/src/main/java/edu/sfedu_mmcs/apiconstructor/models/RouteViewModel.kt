package edu.sfedu_mmcs.apiconstructor.models

import android.content.SharedPreferences
import android.telecom.Call
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo
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

class RouteViewModel (
    private val sp: SharedPreferences
):  ViewModel() {
    val routeList = MutableLiveData<List<RouteInfo>>()
    private val api = Api(
        sp.getString("saved_url", "http://10.0.2.2:8000").toString(),
        sp.getString("saved_spec", "http://10.0.2.2:8000/openapi.json").toString()
    )
    private val client = OkHttpClient()


    fun getSchemaInfo(schema: Schema<*>, openAPI: OpenAPI): List<ContentInfo> {
        val result = ArrayList<ContentInfo>()
        if (schema.`$ref` != null) {
            Log.d("Path parameter","ref: ${schema.`$ref`}")
            result.addAll(resolveSchemaRef(schema.`$ref`, openAPI))
        } else if (schema.type == "array") {
            // Обработка случая, когда схема является массивом
            schema.items?.let { itemSchema ->
                val itemInfo = getSchemaInfo(itemSchema, openAPI)
                val arrayValue = itemInfo.joinToString(", ") { it.example }
                result.add(ContentInfo("array", "[$arrayValue]"))
            }
        } else {
            schema.properties?.forEach { (name, propSchema) ->
                if (propSchema.example != null) {
                    result.add(ContentInfo(name, propSchema.example.toString()))
                } else if (propSchema.examples != null) {
                    result.add(
                        ContentInfo(
                            name,
                            propSchema.examples[0].toString()
                        )
                    )
                }
            }
        }
        return result
    }

    // Function to resolve and print information about a schema reference from openAPI
    fun resolveSchemaRef(ref: String, openAPI: OpenAPI): List<ContentInfo> {
        val refPath = ref.replace("#/components/schemas/", "")
        val schema = openAPI.components?.schemas?.get(refPath)
        val result = ArrayList<ContentInfo>()
        if (schema != null) {
            Log.d("Path parameter","        Resolved schema at: /components/schemas/$refPath")
            Log.d("Path parameter","        Type: ${schema.type}")
            schema.properties?.forEach { (name, propSchema) ->
                if (propSchema.type != "array") {
                    if (propSchema.`$ref` != null) {
                        result.addAll(resolveSchemaRef(propSchema.`$ref`, openAPI))
                    } else {
                        result.add(
                            ContentInfo(
                                name,
                                if (propSchema.example == null) "" else propSchema.example.toString()
                            )
                        )
                    }
                } else {
                    propSchema.items?.let { itemSchema ->
                        val itemValue = if (itemSchema.example != null) {
                            itemSchema.example.toString()
                        } else if (itemSchema.`$ref` != null) {
                            resolveSchemaRef(itemSchema.`$ref`, openAPI).joinToString { it.example }
                        } else {
                            "Array item"
                        }
                        result.add(ContentInfo(name, "[$itemValue]"))
                    }
                }
            }
        } else {
            Log.d("Path parameter","Could not resolve schema for $ref")
        }
        return result
    }

    fun getRoutes() {
        val specRoute = api.getOpenApi();
        val request = Request.Builder()
            .url(specRoute)
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
                            val fieldsArr = ArrayList<ContentInfo>()
                            operation.parameters?.forEach { parameter ->
                                fieldsArr.add(ContentInfo(parameter.name, ""))
                            }
                            val contentFields = ArrayList<ContentInfo>()
                            operation.requestBody?.let { requestBody ->
                                requestBody.content?.forEach { (mediaType, mediaTypeObject) ->
                                    if (mediaType == "application/json")
                                        contentFields.addAll(getSchemaInfo(mediaTypeObject.schema, openAPI))
                                }
                            }
                            routesRes.add(
                                RouteInfo(
                                    pathKey,
                                    httpMethod.name,
                                    if (fieldsArr.size + contentFields.size > 0) "form" else "list",
                                    fieldsArr,
                                    contentFields
                                )
                            )
                        }
                    }
                    routeList.postValue(routesRes)
                }
            }
        })

    }
}