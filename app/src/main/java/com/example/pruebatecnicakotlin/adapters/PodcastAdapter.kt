package com.example.pruebatecnicakotlin.adapters


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicakotlin.R
import com.example.pruebatecnicakotlin.activities.PodcastActivity
import com.example.pruebatecnicakotlin.dataModels.Podcast
import com.squareup.picasso.Picasso

class PodcastAdapter(var contexto: Activity, var podcasts: ArrayList<Podcast>):RecyclerView.Adapter<PodcastAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_podcast, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val podcast = podcasts[position]
        val image = podcast.image.last().url
        Picasso.get().load(image).into(holder.img)

        holder.marco.setOnClickListener {
            var intent = Intent(contexto, PodcastActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable("selectedPodcast", podcast)
            intent.putExtras(bundle)
            contexto.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return podcasts.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val img : ImageView
        val marco: CardView

        init{
            img = view.findViewById(R.id.card_podcast_img)
            marco = view.findViewById(R.id.card_marco)
        }
    }
}