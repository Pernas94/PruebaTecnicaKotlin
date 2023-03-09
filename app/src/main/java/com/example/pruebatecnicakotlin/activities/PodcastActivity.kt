package com.example.pruebatecnicakotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicakotlin.R
import com.example.pruebatecnicakotlin.adapters.EpisodeAdapter
import com.example.pruebatecnicakotlin.dataModels.Episode
import com.example.pruebatecnicakotlin.dataModels.Podcast
import com.example.pruebatecnicakotlin.service.APIService
import com.example.pruebatecnicakotlin.service.ManageFiles
import com.example.pruebatecnicakotlin.service.RetrofitAux
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.exists

class PodcastActivity : AppCompatActivity() {

    //Views
    private lateinit var image : ImageView
    private lateinit var title : TextView
    private lateinit var author: TextView
    private lateinit var summary: TextView
    private lateinit var loadingIcon : ProgressBar

    //Recycler
    private lateinit var recycler: RecyclerView
    private lateinit var adapter:EpisodeAdapter

    //Inits
    var podcast:Podcast? = null
    var episodes:ArrayList<Episode> = arrayListOf()
    private lateinit var FILE_NAME:String
    private lateinit var FILE_PATH:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)


        if(intent!=null){
            podcast = intent.getSerializableExtra("selectedPodcast") as Podcast
            //showInLog(podcast!!)
            initElements(podcast)
        }


        if(Paths.get(FILE_PATH).exists() && ManageFiles.fileValid(86400000, FILE_PATH)){
            Log.i("AppLog", "Archivo encontrado en Files-> "+FILE_PATH)
            ManageFiles.showFileEpisode(episodes, FILE_PATH)

        }else{

            getPodcast("lookup?id=${podcast?.id?.attrib?.id}&entity=podcastEpisode&limit=9") //Llamada podcast completo
        }

        initRecycler()
    }

    private fun initElements(podcast: Podcast?) {

        image = findViewById(R.id.pod_image)
        Picasso.get().load(podcast?.image?.last()?.url).into(image)


        title = findViewById(R.id.pod_title)
        title.text = podcast?.name?.name

        author = findViewById(R.id.pod_author)
        author.text = podcast?.artist?.name

        summary = findViewById(R.id.pod_summary)
        summary.text = podcast?.summary?.summary
        summary.movementMethod = ScrollingMovementMethod() //makes textview scrollable

        loadingIcon = findViewById(R.id.pod_progressBar)

        FILE_NAME = podcast?.name?.name+".json"
        FILE_PATH = filesDir.path+"/"+FILE_NAME
    }


    private fun initRecycler(){

        recycler = findViewById(R.id.pod_recycler)
        adapter = EpisodeAdapter(this, episodes, podcast?.image?.last()!!.url)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun getPodcast(query:String){
        var context = this
        loadingIcon.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch{

            val call = RetrofitAux.getRetrofit().create(APIService::class.java).getFullPodcast(query)
            val response = call.body()

            runOnUiThread {
                if(call.isSuccessful){

                    Log.i("AppLog", "Llamada a API (getPodcast) exitosa.")
                    //Extraigo episodios
                    var aAgregar = response?.results ?: arrayListOf()

                    //Filter anything that is not an episode
                    aAgregar = aAgregar.filter{it.kind == "podcast-episode"} as ArrayList<Episode>


                    ManageFiles.createFile(context, aAgregar, FILE_NAME)

                    episodes.clear()
                    episodes.addAll(aAgregar)
                    adapter.notifyDataSetChanged()
                    loadingIcon.visibility = View.GONE


                }else{
                    Log.e("AppLog", call.errorBody().toString())
                }
            }

        }
    }

}