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
    val image : ImageView by lazy{findViewById(R.id.pod_image)}
    val title : TextView by lazy{findViewById(R.id.pod_title)}
    val author: TextView by lazy{findViewById(R.id.pod_author)}
    val summary: TextView by lazy{findViewById(R.id.pod_summary)}
    val progressBar : ProgressBar by lazy{findViewById(R.id.pod_progressBar)}

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

        summary.movementMethod = ScrollingMovementMethod() //makes textview scrollable

        if(intent!=null){
            podcast = intent.getSerializableExtra("selectedPodcast") as Podcast
            //showInLog(podcast!!)
            initElements(podcast)
        }

        FILE_NAME = podcast?.name?.name+".json"
        FILE_PATH = filesDir.path+"/"+FILE_NAME

        if(Paths.get(FILE_PATH).exists() && ManageFiles.fileValid(86400000, FILE_PATH)){
            Log.d("Mau", "Podcast encontrado en files")
            ManageFiles.showFileEpisode(episodes, FILE_PATH)

        }else{

            getPodcast("lookup?id=${podcast?.id?.attrib?.id}&entity=podcastEpisode&limit=9") //Llamada podcast completo
        }


        initRecycler()
    }

    private fun initElements(podcast: Podcast?) {
        Picasso.get().load(podcast?.image?.last()?.url).into(image)
        title.text = podcast?.name?.name
        author.text = podcast?.artist?.name
        summary.text = podcast?.summary?.summary
    }


    private fun initRecycler(){
        //Filter out the podcast information item [0]


        recycler = findViewById(R.id.pod_recycler)
        adapter = EpisodeAdapter(this, episodes, podcast?.image?.last()!!.url)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun getPodcast(query:String){

        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{


            val call = getRetrofit().create(APIService::class.java).getFullPodcast(query)
            Log.i("Mau", "Llamada podcast completo API-> "+call.toString())
            val response = call.body()

            runOnUiThread {
                if(call.isSuccessful){

                    //Extraigo episodios
                    var aAgregar = response?.results ?: arrayListOf()

                    //Filter anything that is not an episode
                    aAgregar = aAgregar.filter{it.kind == "podcast-episode"} as ArrayList<Episode>


                    createFile(aAgregar, FILE_NAME)

                    episodes.clear()
                    episodes.addAll(aAgregar)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE


                }else{
                    Log.e("Mau", call.errorBody().toString())
                }
            }

        }
    }

    fun createFile(array: ArrayList<Episode>, fileName:String){

        try{
            //Si el archivo no existe, lo creo y lo relleno con la respuesta en formato Json
            //TODO
            val outStream = openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
            val json = Gson().toJson(array)
            outStream.write(json.encodeToByteArray())
            Log.d("Mau", "Archivo creado con "+fileName)
            outStream.close()


        }catch(e: IOException){
            Log.e("MauError", e.message.toString())
        }
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://itunes.apple.com/").
        addConverterFactory(GsonConverterFactory.create()).build()

        //https://itunes.apple.com/us/rss/toppodcasts/limit=100/genre=1310/json
        //https://itunes.apple.com/lookup?id={podcastId}
        //https://itunes.apple.com/lookup?id=1535809341&entity=podcastEpisode&limit=9
    }


}