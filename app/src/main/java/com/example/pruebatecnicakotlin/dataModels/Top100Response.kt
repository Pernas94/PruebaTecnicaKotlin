package com.example.pruebatecnicakotlin.dataModels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Top100Response(@SerializedName("feed") var feed:Feed)

data class Feed (@SerializedName("entry") var podcasts: ArrayList<Podcast>)

data class Podcast(
    @SerializedName("im:name")var name : PodcastName,
    @SerializedName("im:image")var image: ArrayList<PodcastImage>,
    @SerializedName("summary")var summary : PodcastSummary,
    @SerializedName("id") var id: IdInfo,
    @SerializedName("im:artist") var artist:Artist

): Serializable

data class PodcastName(
    @SerializedName("label") var name:String
): Serializable

data class PodcastImage(
    @SerializedName("label") var url:String
): Serializable

data class PodcastSummary(
    @SerializedName("label") var summary:String
): Serializable

data class IdInfo(
    @SerializedName("attributes") var attrib:Attrib,
    @SerializedName("label") var link:String
): Serializable

data class Attrib(
    @SerializedName("im:id") var id:String
): Serializable

data class Artist(
    @SerializedName("label") var name:String
): Serializable