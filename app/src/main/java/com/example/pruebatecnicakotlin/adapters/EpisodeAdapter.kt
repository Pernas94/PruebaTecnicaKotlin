package com.example.pruebatecnicakotlin.adapters


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicakotlin.R
import com.example.pruebatecnicakotlin.activities.EpisodeActivity
import com.example.pruebatecnicakotlin.dataModels.Episode


class EpisodeAdapter(var contexto: Context, var episodes:ArrayList<Episode>, var imageUrl: String): RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_episode, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val episode = episodes[position]
        var auxDate = episode.releaseDate.substring(0,episode.releaseDate.indexOf("T")).split("-")
        var date = auxDate[2]+"/"+auxDate[1]+"/"+auxDate[0]

        var name = episode.trackName
        if(episode.trackName.length>60) {
            var lastSpace= episode.trackName.substring(0, 60).lastIndexOf(" ")
            name = episode.trackName.substring(0,lastSpace)+"..."
        }



        holder.title.text = name
        holder.date.text = date
        holder.duration.text = (episode.duration/1000/60).toString()+" min"

        holder.container.setOnClickListener {
            var intent = Intent (contexto, EpisodeActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable("episode", episode)
            bundle.putString("imageUrl", imageUrl)
            intent.putExtras(bundle)
            contexto.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return episodes.size
    }


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        var title : TextView
        var date: TextView
        var duration : TextView
        var container : ConstraintLayout

        init{
            title = view.findViewById(R.id.episode_title)
            date = view.findViewById(R.id.episode_date)
            duration = view.findViewById(R.id.episode_duration)
            container = view.findViewById(R.id.episode_container)
        }
    }
}