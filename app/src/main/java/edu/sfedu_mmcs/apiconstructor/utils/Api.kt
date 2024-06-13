package edu.sfedu_mmcs.apiconstructor.utils

class Api {

    fun getBaseApi(): String{
        return "http://10.0.2.2:8000"
    }

    fun getApiDescriptionRoute(): String{
        return "http://10.0.2.2:8000/api_description/"
    }
}