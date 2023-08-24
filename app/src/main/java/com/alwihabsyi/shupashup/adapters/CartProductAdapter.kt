package com.alwihabsyi.shupashup.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.databinding.CartProductItemBinding
import com.alwihabsyi.shupashup.util.getProductPrice
import com.bumptech.glide.Glide

class CartProductAdapter :
    RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {
    inner class CartProductsViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBindView(product: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(product.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = product.product.name
                tvCartProductQuantity.text = product.quantity.toString()

                val priceAfterPercentage =
                    product.product.offerPercentage.getProductPrice(product.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        product.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = product.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.onBindView(product)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(product)
        }

        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(product)
        }

        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(product)
        }
    }

    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null

}