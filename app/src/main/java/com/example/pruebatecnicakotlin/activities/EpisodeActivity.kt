package com.example.pruebatecnicakotlin.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import com.example.pruebatecnicakotlin.R
import com.example.pruebatecnicakotlin.dataModels.Episode
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodeActivity : AppCompatActivity() {


    //Views
    private lateinit var ivImage: ImageView
    private lateinit var skSeekbar: SeekBar
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var btnPlay: ImageButton
    private lateinit var progressBar: ProgressBar



    //Inits
    private lateinit var episode: Episode
    private lateinit var podcastImg:String
    private lateinit var mediaPlayer: MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)

        if(intent!=null){
            @Suppress("DEPRECATION")
            episode = intent.getSerializableExtra("episode") as Episode
            podcastImg = intent.extras?.getString("imageUrl") as String
        }


        initElements()
        mediaPlayer  = MediaPlayer()

        //Launch audio corroutine to prepare the MediaPlayer async
        audioCorroutine()

        //Set behaviour of Play/Pause button
        playPauseAudio()

    }




    private fun initElements() {

        ivImage = findViewById(R.id.ep_image)
        Picasso.get().load(podcastImg).into(ivImage)
        ivImage.setOnClickListener {
            onBackPressed()
        }

        skSeekbar = findViewById(R.id.ep_seekbar)
        skSeekbar.progress = 0
        //skSeekbar.max = (episode.duration).toInt()
        skSeekbar.max = episode.duration.toInt()

        seekbarEvent()


        txtTitle = findViewById(R.id.ep_title)
        txtTitle.text = episode.trackName

        txtDescription = findViewById(R.id.ep_description)
        txtDescription.text = episode.shortDescription.ifEmpty { episode.fullDescription }
        txtDescription.movementMethod = ScrollingMovementMethod()

        btnPlay = findViewById(R.id.ep_playButton)
        btnPlay.isEnabled = false


        progressBar = findViewById(R.id.ep_progressBar)
        progressBar.visibility = View.VISIBLE


    }



    private fun audioCorroutine() {
        var context = this
        CoroutineScope(Dispatchers.IO).launch {

            try{
                mediaPlayer.setDataSource(episode.audioUrl)
                mediaPlayer.prepare()
            }catch (e:Exception){
                Log.i("AppLog", e.message.toString())
                Toast.makeText(context, "Error cargando el audio", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }


            mediaPlayer.setOnPreparedListener {

                runOnUiThread {
                    btnPlay.isEnabled = true
                    btnPlay.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }
    }


    private fun playPauseAudio() {
        btnPlay.setOnClickListener {
            if(!mediaPlayer.isPlaying){

                mediaPlayer.start()
                btnPlay.setImageResource(R.drawable.ic_baseline_pause_circle_50)

            }else{
                mediaPlayer.pause()
                btnPlay.setImageResource(R.drawable.ic_baseline_play_circle_50)
            }
        }
    }

    fun seekbarEvent(){

        skSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if(changed){
                    mediaPlayer.seekTo(pos)
                }
            }


            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
    }


    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.stop()
        mediaPlayer.release()

    }
}