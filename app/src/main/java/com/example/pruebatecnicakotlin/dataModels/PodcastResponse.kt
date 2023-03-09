package com.example.pruebatecnicakotlin.dataModels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PodcastResponse(
    @SerializedName("results") var results:ArrayList<Episode>
):Serializable

data class Episode(
    @SerializedName("trackId")var trackId:Long,
    @SerializedName("kind")var kind:String,
    @SerializedName("trackName")var trackName:String,//titulo
    @SerializedName("releaseDate")var releaseDate:String,
    @SerializedName("trackTimeMillis")var duration:Long,
    @SerializedName("episodeUrl")var audioUrl:String,
    @SerializedName("shortDescription")var shortDescription:String,
    @SerializedName("description")var fullDescription:String
):Serializable