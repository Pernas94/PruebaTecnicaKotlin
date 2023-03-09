package com.example.pruebatecnicakotlin.service

import com.example.pruebatecnicakotlin.dataModels.Top100Response
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url


interface APIService {

    @GET
    suspend fun getTop100Podcasts(@Url url:String): Response<Top100Response>

}