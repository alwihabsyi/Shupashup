package com.alwihabsyi.shupashup.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.databinding.ColorRvItemBinding
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    private var selectedPosition = -1

    inner class ColorViewHolder(val binding: ColorRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBindView(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)
            if (position == selectedPosition) {//color is selected
                binding.apply {
                    imageShadow.show()
                    icCheck.show()
                }
            } else {//color is not selected
                binding.apply {
                    imageShadow.hide()
                    icCheck.hide()
                }
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            ColorRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.onBindView(color, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(color)
        }
    }

    var onItemClick: ((Int) -> Unit)? = null

}