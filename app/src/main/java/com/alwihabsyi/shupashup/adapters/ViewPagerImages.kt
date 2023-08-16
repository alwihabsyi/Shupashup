package com.alwihabsyi.shupashup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alwihabsyi.shupashup.databinding.ViewpagerImageItemBinding
import com.bumptech.glide.Glide

class ViewPagerImages: RecyclerView.Adapter<ViewPagerImages.ViewPagerImagesViewHolder>() {

    inner class ViewPagerImagesViewHolder(val binding: ViewpagerImageItemBinding): ViewHolder(binding.root) {
        fun onBindView(image: String) {
            Glide.with(itemView).load(image).into(binding.imageProductDetails)
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerImagesViewHolder {
        return ViewPagerImagesViewHolder(
            ViewpagerImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewPagerImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.onBindView(image)
    }

}