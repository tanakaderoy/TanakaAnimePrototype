package com.tanaka.mazivanhanga.tanakaanimeprototype.views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tanaka.mazivanhanga.tanakaanimeprototype.Constants.SHOW_EPISODE_URL
import com.tanaka.mazivanhanga.tanakaanimeprototype.R
import com.tanaka.mazivanhanga.tanakaanimeprototype.VideoActivity
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.LatestShowListItemBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import kotlinx.android.synthetic.main.latest_show_list_item.view.*


/**
 * Created by Tanaka Mazivanhanga on 08/07/2020
 */
class LatestEpisodeAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LatestShow>() {

        override fun areItemsTheSame(oldItem: LatestShow, newItem: LatestShow): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: LatestShow, newItem: LatestShow): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return LatestEpisodeViewHolder(
            null,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.latest_show_list_item, parent, false),
            interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val show = differ.currentList[position]
        when (holder) {
            is LatestEpisodeViewHolder -> {
                holder.bind(show)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<LatestShow>) {
        differ.submitList(list)
    }

    class LatestEpisodeViewHolder
    constructor(
        val binding: LatestShowListItemBinding?,
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(show: LatestShow) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, show)
            }
            current_ep_text_view.text = show.currentEp
            title_text_view.text = show.title
            Glide.with(itemView).load(show.image).into(poster_image_view)
            go_button.setOnClickListener {
                Toast.makeText(itemView.context, show.currentEpURL, Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra(SHOW_EPISODE_URL, show.currentEpURL)
                itemView.context.startActivity(intent)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: LatestShow)
    }
}
