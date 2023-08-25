package com.alwihabsyi.shupashup.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.databinding.BillingProductsRvItemBinding
import com.alwihabsyi.shupashup.util.getProductPrice
import com.bumptech.glide.Glide

class BillingProductAdapter: Adapter<BillingProductAdapter.BillingProductsViewholder>() {

    inner class BillingProductsViewholder(val binding: BillingProductsRvItemBinding): ViewHolder(binding.root){
        fun onBindViewHolder(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterPercentage =
                    billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                tvProductCartPrice.text = "$ ${String.format("%.2f", priceAfterPercentage)}"

                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        billingProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = billingProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    private val diffUtil = object: DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewholder {
        return BillingProductsViewholder(
            BillingProductsRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: BillingProductsViewholder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.onBindViewHolder(billingProduct)
    }

    override fun getItemCount(): Int = differ.currentList.size

//    var onClick: ((CartProduct) -> Unit)? = null

}