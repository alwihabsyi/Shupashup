package com.alwihabsyi.shupashup.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.databinding.ColorRvItemBinding
import com.alwihabsyi.shupashup.databinding.SizesRvItemBinding
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    inner class SizesViewHolder(val binding: SizesRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBindView(size: String, position: Int) {
            binding.tvSize.text = size
            if (position == selectedPosition) {//size is selected
                binding.apply {
                    imageShadow.show()
                }
            } else {//size is not selected
                binding.apply {
                    imageShadow.hide()
                }
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizesRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.onBindView(size, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick: ((String) -> Unit)? = null
}