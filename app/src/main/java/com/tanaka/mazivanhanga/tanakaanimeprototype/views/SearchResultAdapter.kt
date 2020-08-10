package com.tanaka.mazivanhanga.tanakaanimeprototype.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tanaka.mazivanhanga.tanakaanimeprototype.R
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.SearchResult
import kotlinx.android.synthetic.main.search_result_list_item.view.*


/**
 * Created by Tanaka Mazivanhanga on 08/09/2020
 */
class SearchResultAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchResult>() {

        override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SearchResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_result_list_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchResultViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<SearchResult>) {
        differ.submitList(list)
    }

    class SearchResultViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SearchResult) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            search_title.text = item.title
            release_date.text = item.releaseYear
            search_subtitle.text = item.subtitle
            Glide.with(itemView).load(item.poster).into(search_poster)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: SearchResult)
    }
}
