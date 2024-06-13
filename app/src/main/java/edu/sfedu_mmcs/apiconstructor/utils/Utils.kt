package edu.sfedu_mmcs.apiconstructor.utils

fun replacePlaceholders(template: String, valuesMap: Map<String, Any>): String {
    var result = template

    val regex = "\\{(.*?)\\}".toRegex()

    result = regex.replace(result) { matchResult ->
        val key = matchResult.groupValues[1]
        valuesMap[key].toString() ?: matchResult.value
    }

    return result
}