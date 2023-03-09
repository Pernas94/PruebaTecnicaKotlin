package com.example.pruebatecnicakotlin.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitAux {

    companion object{
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl("https://itunes.apple.com/").
            addConverterFactory(GsonConverterFactory.create()).build()

            //https://itunes.apple.com/us/rss/toppodcasts/limit=100/genre=1310/json
            //https://itunes.apple.com/lookup?id={podcastId}
            //https://itunes.apple.com/lookup?id=1535809341&entity=podcastEpisode&limit=9
        }
    }

}