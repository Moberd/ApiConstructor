package edu.sfedu_mmcs.apiconstructor.utils

class Api (
    private val base: String,
    private val openApiRoute: String
){

    fun getBaseApi(): String{
        return base
    }

    fun getOpenApi(): String{
        return openApiRoute
    }
}