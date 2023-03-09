package com.example.pruebatecnicakotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicakotlin.adapters.PodcastAdapter
import com.example.pruebatecnicakotlin.dataModels.Podcast
import com.example.pruebatecnicakotlin.service.APIService
import com.example.pruebatecnicakotlin.service.ManageFiles
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.exists

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    //Views
    private lateinit var recycler: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar


    //Inits
    private var podcasts = arrayListOf<Podcast>()
    private var shownPodcasts = arrayListOf<Podcast>()
    private lateinit var adapter: PodcastAdapter
    private lateinit var FILE_PATH:String
    private val FILE_NAME = "jsonPodcasts.json"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchView= findViewById(R.id.main_svPodcasts)
        progressBar = findViewById(R.id.main_progressBar)

        searchView.setOnQueryTextListener(this)//link onQueryListener to Searchview
        FILE_PATH = filesDir.path+"/"+FILE_NAME

        //if(FileController.fileValid(86400000, FILE_PATH)){ //86400000 == 24 horas
        //If the file exists and it's been created in the last 24 hours, we display it directly
        if(Paths.get(FILE_PATH).exists() && ManageFiles.fileValid(86400000, FILE_PATH)) {

            ManageFiles.showFilePodcast(podcasts, FILE_PATH)

            //If it doesn't exist or it's older than 24 hours, we call the API Service, display it and save the data in a specific file
        }else{

            getTopPodcasts("us/rss/toppodcasts/limit=100/genre=1310/json")
        }


        initRecycler()
    }





    private fun initRecycler(){
        shownPodcasts = podcasts
        recycler = findViewById(R.id.main_recyclPodcasts)
        adapter = PodcastAdapter(this, shownPodcasts)
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = adapter
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://itunes.apple.com/").
        addConverterFactory(GsonConverterFactory.create()).build()

        //https://itunes.apple.com/us/rss/toppodcasts/limit=100/genre=1310/json
        //https://itunes.apple.com/lookup?id={podcastId}
        //https://itunes.apple.com/lookup?id=1535809341&entity=podcastEpisode&limit=9
    }

    private fun getTopPodcasts(query:String){

        Toast.makeText(this, "Landanzo corrutina", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {

            progressBar.visibility = View.VISIBLE
            val call = getRetrofit().create(APIService::class.java).getTop100Podcasts(query)
            val response = call.body()

            runOnUiThread { //Permite ejecutar algo en el hilo principal, aún estando en corrutina
                if(call.isSuccessful){

                    val aAgregar = response?.feed?.podcasts ?: arrayListOf()

                    createFile(aAgregar, FILE_NAME)

                    podcasts.clear()
                    podcasts.addAll(aAgregar)

                    /*for(pod in podcasts){
                        Log.e("Mau", pod.name.name+"-> "+pod.id.attrib.id)
                    }*/
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE


                }else{
                    Log.e("Mau", call.errorBody().toString())
                }
            }

        }



    }

    fun createFile(array: ArrayList<Podcast>, fileName:String){

        try{
            Toast.makeText(this, "Creating file $fileName", Toast.LENGTH_SHORT).show()

            //Si eldass archivo no existe, lo creo y lo relleno con la respuesta en formato Json
            //TODO
            val outStream = this@MainActivity.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
            val json = Gson().toJson(array)
            outStream.write(json.encodeToByteArray())
            outStream.close()


        }catch(e: IOException){
            Log.e("MauError", e.message.toString())
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        shownPodcasts = podcasts.filter { (it.name.name +" "+ it.artist.name).lowercase().contains(""+newText) } as ArrayList<Podcast>

        //TODO
        adapter = PodcastAdapter(this, shownPodcasts)
        recycler.adapter = adapter
        return true
    }
}