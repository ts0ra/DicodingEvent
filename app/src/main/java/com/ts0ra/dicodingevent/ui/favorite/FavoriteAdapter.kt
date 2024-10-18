package com.ts0ra.dicodingevent.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ts0ra.dicodingevent.database.FavoriteEvent
import com.ts0ra.dicodingevent.databinding.ItemEventBinding
import com.ts0ra.dicodingevent.helper.EventDiffCallback

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    interface OnItemClickCallback {
        fun onItemClicked(event: FavoriteEvent)
    }

    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listNotes = ArrayList<FavoriteEvent>()

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        this.onItemClickCallback = callback
    }

    fun setListFavoriteEvents(listNotes: List<FavoriteEvent>) {
        val diffCallback = EventDiffCallback(this.listNotes, listNotes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listNotes.clear()
        this.listNotes.addAll(listNotes)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(listNotes[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listNotes[position])
        }
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    inner class FavoriteViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEvent) {
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.imgItemBanner)
        }
    }
}