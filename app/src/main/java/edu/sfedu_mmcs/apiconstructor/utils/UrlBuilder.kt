package edu.sfedu_mmcs.apiconstructor.utils

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object UrlBuilder {
    fun buildUrl(baseUrl: String, endpoint: String, data: Map<String, String>): String {
        var updatedEndpoint = endpoint
        val usedKeys = mutableSetOf<String>()

        val placeholderRegex = Regex("\\{([^{}]+)\\}")
        placeholderRegex.findAll(endpoint).forEach { match ->
            val key = match.groupValues[1]
            if (data.containsKey(key)) {
                updatedEndpoint = updatedEndpoint.replace("{${key}}", data[key] ?: "")
                usedKeys.add(key)
            }
        }

        val base = baseUrl.removeSuffix("/")
        val path = updatedEndpoint.removePrefix("/")
        val urlBuilder = "$base/$path".toHttpUrlOrNull()?.newBuilder()
            ?: throw IllegalArgumentException("Invalid base URL or endpoint: $base/$path")

        data.filter { (key, _) -> !usedKeys.contains(key) }
            .forEach { (key, value) ->
                urlBuilder.addQueryParameter(key, value)
            }

        return urlBuilder.build().toString()
    }
}