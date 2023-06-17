package com.mawumbo.mystoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mawumbo.mystoryapp.R
import com.mawumbo.mystoryapp.databinding.ListItemStoryBinding
import com.mawumbo.mystoryapp.model.Story

class StoryListAdapter(private val onClick: (String?) -> Unit) :
    ListAdapter<Story, StoryListAdapter.StoryViewHolder>(StoryDiffCallback) {

    class StoryViewHolder(private val binding: ListItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story, onClick: (String?) -> Unit) {
            binding.apply {
                itemStoryContainer.setOnClickListener {
                    onClick(item.id)
                }

                tvItemName.text = item.name
                tvItemDescription.text = item.description
                Glide.with(root.context)
                    .load(item.photoUrl)
                    .into(ivItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ListItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user, onClick)
            holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(
                    holder.itemView.context,
                    R.anim.scale_up
                )
            )
        }
    }

}

object StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }

}