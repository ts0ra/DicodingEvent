package com.ts0ra.dicodingevent.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ts0ra.dicodingevent.data.database.local.entity.EventEntity
import com.ts0ra.dicodingevent.data.database.remote.reponse.Event
import com.ts0ra.dicodingevent.data.database.remote.reponse.ListEventsItem
import com.ts0ra.dicodingevent.databinding.CarouselLayoutBinding
import com.ts0ra.dicodingevent.databinding.FinishedCardEventBinding
import com.ts0ra.dicodingevent.databinding.ItemEventBinding
import com.ts0ra.dicodingevent.ui.activity.DetailActivity

class EventAdapter(
    private val viewType: Int,
    private val onItemClick: OnItemClickListener
) : ListAdapter<EventEntity, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(event: EventEntity)
    }

    // ViewHolder for Home Upcoming Events
    class HomeUpcomingEventViewHolder(private val binding: CarouselLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.carouselTextView.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.carouselImageView)
//            itemView.setOnClickListener {
//                val intent = Intent(binding.root.context, DetailActivity::class.java)
//                intent.putExtra(EVENT_ID, event.id)
//                binding.root.context.startActivity(intent)
//            }
        }
    }

    // ViewHolder for Home Finished Events
    class HomeFinishedEventViewHolder(private val binding: FinishedCardEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.imageLogo).into(binding.imgItemBanner)
//            itemView.setOnClickListener {
//                val intent = Intent(binding.root.context, DetailActivity::class.java)
//                intent.putExtra(EVENT_ID, event.id)
//                binding.root.context.startActivity(intent)
//            }
        }
    }

    // ViewHolder for Upcoming Events
    class UpcomingEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.imgItemBanner)
//            itemView.setOnClickListener {
//                val intent = Intent(binding.root.context, DetailActivity::class.java)
//                intent.putExtra(EVENT_ID, event.id)
//                binding.root.context.startActivity(intent)
//            }
        }
    }

    // ViewHolder for Upcoming Events
    class FinishedEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.tvItemTitle.text = event.name
            binding.tvItemTitle.maxLines = 3
            binding.tvItemTitle.ellipsize = android.text.TextUtils.TruncateAt.END
            Glide.with(binding.root.context).load(event.imageLogo).into(binding.imgItemBanner)
//            itemView.setOnClickListener {
//                val intent = Intent(binding.root.context, DetailActivity::class.java)
//                intent.putExtra(EVENT_ID, event.id)
//                binding.root.context.startActivity(intent)
//            }
        }
    }

    // ViewHolder for Favorite Events
    class FavoriteEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            binding.tvItemTitle.text = event.name
            Glide.with(binding.root.context).load(event.mediaCover).into(binding.imgItemBanner)
//            itemView.setOnClickListener {
//                val intent = Intent(binding.root.context, DetailActivity::class.java)
//                intent.putExtra(EVENT_ID, event.id)
//                binding.root.context.startActivity(intent)
//            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            0 -> HOME_UPCOMING_EVENT_VIEW_TYPE
            1 -> HOME_FINISHED_EVENT_VIEW_TYPE
            2 -> UPCOMING_EVENT_VIEW_TYPE
            3 -> FINISHED_EVENT_VIEW_TYPE
            4 -> FAVORITE_EVENT_VIEW_TYPE
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
            FAVORITE_EVENT_VIEW_TYPE -> {
                val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FavoriteEventViewHolder(binding)
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

            is FavoriteEventViewHolder -> {
                holder.bind(event)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick.onItemClick(event)
        }
    }

    companion object {
        const val EVENT_ID = "event_id"

        const val HOME_UPCOMING_EVENT_VIEW_TYPE = 0
        const val HOME_FINISHED_EVENT_VIEW_TYPE = 1
        const val UPCOMING_EVENT_VIEW_TYPE = 2
        const val FINISHED_EVENT_VIEW_TYPE = 3
        const val FAVORITE_EVENT_VIEW_TYPE = 4

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
