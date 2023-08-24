package com.alwihabsyi.shupashup.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.data.Product
import com.alwihabsyi.shupashup.databinding.ProductRvItemBinding
import com.alwihabsyi.shupashup.util.getProductPrice
import com.bumptech.glide.Glide

class BestProductAdapter: RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {
    inner class BestProductViewHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBindView(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if(product.offerPercentage == null){
                    tvNewPrice.visibility = View.INVISIBLE
                }
                tvPrice.text = "$ ${product.price}"
                tvName.text = product.name
            }
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }
    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.onBindView(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick:((Product) -> Unit)? = null
}