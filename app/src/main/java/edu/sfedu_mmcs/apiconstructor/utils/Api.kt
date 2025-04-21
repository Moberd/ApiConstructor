package edu.sfedu_mmcs.apiconstructor.utils

class Api {

    private val baseRoute = "http://10.0.2.2:8000"
    fun getBaseApi(): String{
        return "http://10.0.2.2:8000"
    }

    fun getApiDescriptionRoute(): String{
        return "http://10.0.2.2:8000/api_description/"
    }

    fun getOpenApi(): String{
        return "http://10.0.2.2:8000/openapi.json"
    }
}