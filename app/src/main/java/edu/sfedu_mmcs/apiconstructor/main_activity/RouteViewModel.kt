package edu.sfedu_mmcs.apiconstructor.main_activity

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.sfedu_mmcs.apiconstructor.utils.Api
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo
import edu.sfedu_mmcs.apiconstructor.utils.Endpoint
import edu.sfedu_mmcs.apiconstructor.utils.MethodItem
import edu.sfedu_mmcs.apiconstructor.utils.RouteGroup
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

class RouteViewModel(
    private val sp: SharedPreferences
) : ViewModel() {
    val routeGroups = MutableLiveData<List<RouteGroup>>()
    val isLoading = MutableLiveData<Boolean>()
    private val api = Api(
        sp.getString("saved_url", "http://10.0.2.2:8000").toString(),
        sp.getString("saved_spec", "http://10.0.2.2:8000/openapi.json").toString()
    )
    private val client = OkHttpClient()

    fun getSchemaInfo(schema: Schema<*>, openAPI: OpenAPI): List<ContentInfo> {
        val result = ArrayList<ContentInfo>()
        if (schema.`$ref` != null) {
            result.addAll(resolveSchemaRef(schema.`$ref`, openAPI))
        } else if (schema.type == "object") {
            schema.properties?.forEach { (name, propSchema) ->
                val required = schema.required?.contains(name) ?: false
                if (propSchema.`$ref` != null) {
                    result.addAll(resolveSchemaRef(propSchema.`$ref`, openAPI).map { ContentInfo(it.name, it.example, it.format, it.enumValues, required) })
                } else if (propSchema.type == "object") {
                    result.addAll(getSchemaInfo(propSchema, openAPI).map { ContentInfo(it.name, it.example, it.format, it.enumValues, required) })
                } else if (propSchema.type == "array") {
                    propSchema.items?.let { itemSchema ->
                        val itemExample = itemSchema.example?.toString() ?: "item"
                        val format = propSchema.format ?: propSchema.type
                        result.add(ContentInfo(name, "[$itemExample]", format, propSchema.enum?.map { it.toString() }, required))
                    }
                } else {
                    val example = propSchema.example?.toString() ?: propSchema.examples?.firstOrNull()?.toString() ?: ""
                    val format = propSchema.format ?: propSchema.type
                    val enumValues = propSchema.enum?.map { it.toString() }
                    result.add(ContentInfo(name, example, format, enumValues, required))
                }
            }
        } else if (schema.type == "array") {
            schema.items?.let { itemSchema ->
                val itemInfo = getSchemaInfo(itemSchema, openAPI)
                val arrayValue = itemInfo.joinToString(", ") { it.example }
                val format = schema.format ?: schema.type
                result.add(ContentInfo("array", "[$arrayValue]", format, schema.enum?.map { it.toString() }))
            }
        } else {
            val example = schema.example?.toString() ?: schema.examples?.firstOrNull()?.toString() ?: ""
            val format = schema.format ?: schema.type
            result.add(ContentInfo("body", example, format, schema.enum?.map { it.toString() }))
        }
        return result
    }

    fun resolveSchemaRef(ref: String, openAPI: OpenAPI): List<ContentInfo> {
        val refPath = ref.replace("#/components/schemas/", "")
        val schema = openAPI.components?.schemas?.get(refPath)
        val result = ArrayList<ContentInfo>()
        if (schema != null) {
            schema.properties?.forEach { (name, propSchema) ->
                val required = schema.required?.contains(name) ?: false
                if (propSchema.`$ref` != null) {
                    result.addAll(resolveSchemaRef(propSchema.`$ref`, openAPI).map { ContentInfo(it.name, it.example, it.format, it.enumValues, required) })
                } else if (propSchema.type == "array") {
                    propSchema.items?.let { itemSchema ->
                        val itemValue = itemSchema.example?.toString() ?: "item"
                        val format = propSchema.format ?: propSchema.type
                        result.add(ContentInfo(name, "[$itemValue]", format, propSchema.enum?.map { it.toString() }, required))
                    }
                } else {
                    val example = propSchema.example?.toString() ?: ""
                    val format = propSchema.format ?: propSchema.type
                    val enumValues = propSchema.enum?.map { it.toString() }
                    result.add(ContentInfo(name, example, format, enumValues, required))
                }
            }
        } else {
            Log.d("Path parameter", "Could not resolve schema for $ref")
        }
        return result
    }

    fun getRoutes() {
        isLoading.postValue(true)
        val specRoute = api.getOpenApi()
        val request = Request.Builder()
            .url(specRoute)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                isLoading.postValue(false)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        isLoading.postValue(false)
                        throw IOException("Запрос к серверу не был успешен: ${response.code} ${response.message}")
                    }
                    val specContent = response.body!!.string()
                    val parser = OpenAPIV3Parser()
                    val result: SwaggerParseResult = parser.readContents(specContent, null, null)
                    val openAPI = result.openAPI
                    val routesRes = mutableMapOf<String, MutableList<RouteInfo>>()

                    for ((pathKey, pathItem) in openAPI.paths) {
                        pathItem.readOperationsMap().forEach { (httpMethod, operation) ->
                            val fieldsArr = ArrayList<ContentInfo>()
                            operation.parameters?.forEach { parameter ->
                                val schema = parameter.schema
                                if (schema != null) {
                                    val example = schema.example?.toString() ?: ""
                                    val format = schema.format ?: schema.type
                                    val enumValues = schema.enum?.map { it.toString() }
                                    val required = parameter.required ?: false
                                    fieldsArr.add(ContentInfo(parameter.name, example, format, enumValues, required))
                                } else {
                                    fieldsArr.add(ContentInfo(parameter.name, "", null, null, parameter.required ?: false))
                                }
                            }
                            val secArr = ArrayList<String>()
                            operation.security?.forEach { sec ->
                                secArr.addAll(sec.keys)
                            }
                            val contentFields = ArrayList<ContentInfo>()
                            operation.requestBody?.let { requestBody ->
                                requestBody.content?.forEach { (mediaType, mediaTypeObject) ->
                                    if (mediaType == "application/json")
                                        contentFields.addAll(getSchemaInfo(mediaTypeObject.schema, openAPI))
                                }
                            }
                            val respMap = HashMap<String, String>()
                            operation.responses?.forEach { op ->
                                respMap[op.key] = op.value.description
                            }
                            val routeInfo = RouteInfo(
                                pathKey,
                                httpMethod.name,
                                if (fieldsArr.size + contentFields.size > 0) "form" else "list",
                                fieldsArr,
                                contentFields,
                                secArr,
                                respMap,
                                operation.description ?: ""
                            )
                            routesRes.getOrPut(pathKey) { mutableListOf() }.add(routeInfo)
                        }
                    }

                    var prefix = ""
                    for (i in 0 until routesRes.keys.minOf { it.length }) {
                        val char = routesRes.keys.elementAt(0)[i]
                        if (routesRes.keys.all { it[i] == char }) {
                            prefix += char
                        } else {
                            break
                        }
                    }

                    val groups = mutableMapOf<String, MutableList<Endpoint>>()
                    routesRes.forEach { (path, routeInfos) ->
                        val groupName = path.split("/")[prefix.count { it == '/' }].takeIf { it.isNotEmpty() } ?: "Miscellaneous"
                        val methods = routeInfos.map { MethodItem(it, it.method) }
                        val endpoint = Endpoint(path, methods)
                        groups.getOrPut(groupName) { mutableListOf() }.add(endpoint)
                    }

                    val routeGroupsList = groups.map { (groupName, endpoints) ->
                        RouteGroup(
                            groupName,
                            endpoints.sortedWith(
                                compareBy(
                                    { it.isSingleMethod },
                                    { it.path }
                                )
                            )
                        )
                    }.sortedBy { it.groupName }

                    routeGroups.postValue(routeGroupsList)
                    isLoading.postValue(false)
                }
            }
        })
    }
}