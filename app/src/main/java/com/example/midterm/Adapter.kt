package com.example.midterm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.databinding.HomePageItemBinding

class Adapter (): ListAdapter<PostData, Adapter.PostViewHolder>(DiffCallback) {

    class PostViewHolder(val binding:HomePageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PostData) {
            binding.post = data
            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<PostData>() {
        override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val data = getItem(position)
        val context = holder.itemView.context
        when (data.category) {
            "SchoolLife" -> holder.binding.category.setTextColor(ContextCompat.getColor(context, R.color.others))
            "Beauty" -> holder.binding.category.setTextColor(ContextCompat.getColor(context, R.color.beauty))
            "Gossiping" -> holder.binding.category.setTextColor(ContextCompat.getColor(context, R.color.gossip))
            else -> holder.binding.category.setTextColor(ContextCompat.getColor(context, R.color.others))
        }
        holder.bind(data)

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        return PostViewHolder(
            HomePageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }




}