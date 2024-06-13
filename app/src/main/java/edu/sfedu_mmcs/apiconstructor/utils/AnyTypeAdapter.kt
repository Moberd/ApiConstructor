package edu.sfedu_mmcs.apiconstructor.utils

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class AnyTypeAdapter : JsonSerializer<Any> {
    override fun serialize(src: Any?, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        return when (src) {
            null -> JsonPrimitive("null")
            is Number -> JsonPrimitive(src)
            is Boolean -> JsonPrimitive(src)
            is String -> JsonPrimitive(src)
            else -> JsonPrimitive(src.toString()) // Default to string representation
        }
    }
}