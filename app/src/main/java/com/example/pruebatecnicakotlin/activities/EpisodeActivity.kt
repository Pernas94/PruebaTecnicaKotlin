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
    private var audioPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)

        if(intent!=null){
            @Suppress("DEPRECATION")
            episode = intent.getSerializableExtra("episode") as Episode
            podcastImg = intent.extras?.getString("imageUrl") as String
        }

        Log.d("Mau", episode.audioUrl)


        initElements()

        mediaPlayer  = MediaPlayer()


        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {

            mediaPlayer.setDataSource(episode.audioUrl)
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                runOnUiThread {
                    btnPlay.isEnabled = true
                    btnPlay.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }




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

    private fun initElements() {

        ivImage = findViewById(R.id.ep_image)
        Picasso.get().load(podcastImg).into(ivImage)

        skSeekbar = findViewById(R.id.ep_seekbar)

        //seekbarEvent()


        txtTitle = findViewById(R.id.ep_title)
        txtTitle.text = episode.trackName

        txtDescription = findViewById(R.id.ep_description)
        txtDescription.text = episode.shortDescription.ifEmpty { episode.fullDescription }
        txtDescription.movementMethod = ScrollingMovementMethod()

        btnPlay = findViewById(R.id.ep_playButton)
        btnPlay.isEnabled = false

        progressBar = findViewById(R.id.ep_progressBar)

        //mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }



    fun seekbarEvent(){
        skSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if(changed){
                    Log.i("Mau", "Mediaplayer.seekTo()-> $pos")
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