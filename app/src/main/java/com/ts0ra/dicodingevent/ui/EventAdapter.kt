package com.ts0ra.dicodingevent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ts0ra.dicodingevent.data.reponse.ListEventsItem
import com.ts0ra.dicodingevent.databinding.CarouselLayoutBinding
import com.ts0ra.dicodingevent.databinding.FinishedCardEventBinding
import com.ts0ra.dicodingevent.databinding.ItemEventBinding

class EventAdapter(private val viewType: Int) : ListAdapter<ListEventsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickCallback {
        fun onItemClicked(event: ListEventsItem)
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        this.onItemClickCallback = callback
    }

    companion object {
        const val EVENT_ID = "event_id"

        const val HOME_UPCOMING_EVENT_VIEW_TYPE = 0
        const val HOME_FINISHED_EVENT_VIEW_TYPE = 1
        const val UPCOMING_EVENT_VIEW_TYPE = 2
        const val FINISHED_EVENT_VIEW_TYPE = 3

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            0 -> HOME_UPCOMING_EVENT_VIEW_TYPE
            1 -> HOME_FINISHED_EVENT_VIEW_TYPE
            2 -> UPCOMING_EVENT_VIEW_TYPE
            3 -> FINISHED_EVENT_VIEW_TYPE
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            HOME_UPCOMING_EVENT_VIEW_TYPE -> {
                val binding = CarouselLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HomeUpcomingEventViewHolder(binding)
            }
            HOME_FINISHED_EVENT_VIEW_TYPE -> {
                val binding = FinishedCardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HomeFinishedEventViewHolder(binding)
            }
            UPCOMING_EVENT_VIEW_TYPE -> {
                val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UpcomingEventViewHolder(binding)
            }
            FINISHED_EVENT_VIEW_TYPE -> {
                val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FinishedEventViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        when (holder) {
            is HomeUpcomingEventViewHolder -> {
                holder.bind(event)
            }

            is HomeFinishedEventViewHolder -> {
                holder.bind(event)
            }

            is UpcomingEventViewHolder -> {
                holder.bind(event)
            }

            is FinishedEventViewHolder -> {
                holder.bind(event)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(event)
        }
    }

    // ViewHolder for Home Upcoming Events
    class HomeUpcomingEventViewHolder(private val binding: CarouselLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.carouselTextView.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.carouselImageView)
        }
    }

    // ViewHolder for Home Finished Events
    class HomeFinishedEventViewHolder(private val binding: FinishedCardEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.imageLogo).into(binding.imgItemBanner)
        }
    }

    // ViewHolder for Upcoming Events
    class UpcomingEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem){
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.imgItemBanner)
        }
    }

    // ViewHolder for Upcoming Events
    class FinishedEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem){
            binding.tvItemTitle.text = event.name
            binding.tvItemTitle.maxLines = 3
            binding.tvItemTitle.ellipsize = android.text.TextUtils.TruncateAt.END
            Glide.with(binding.root.context).load(event.imageLogo).into(binding.imgItemBanner)
        }
    }
}
