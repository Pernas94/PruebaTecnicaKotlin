package com.example.pruebatecnicakotlin.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebatecnicakotlin.dataModels.Podcast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

class ManageFiles {

    companion object{

        fun fileValid(millis:Long, filePath:String):Boolean{

            val path: Path = Paths.get(filePath)
            var file: File = path.toFile()
            return (millis>System.currentTimeMillis()-file.lastModified())
        }

        fun showFilePodcast(podcasts: ArrayList<Podcast>, filePath:String) {

            val arrayListPodcastType = object : TypeToken<ArrayList<Podcast>>() {}.type
            var aAgregar: ArrayList<Podcast> = Gson().fromJson(FileReader(filePath), arrayListPodcastType)
            podcasts.clear()
            podcasts.addAll(aAgregar)
        }


        fun createFile(context: Context, array: ArrayList<Podcast>, fileName:String){

            try{
                //Si el archivo no existe, lo creo y lo relleno con la respuesta en formato Json
                val outStream = context.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
                val json = Gson().toJson(array)
                outStream.write(json.encodeToByteArray())
                outStream.close()


            }catch(e: IOException){
                Log.e("MauError", e.message.toString())
            }
        }

    }

}