package com.alwihabsyi.shupashup.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.data.order.Order
import com.alwihabsyi.shupashup.data.order.OrderStatus
import com.alwihabsyi.shupashup.data.order.getOrderStatus
import com.alwihabsyi.shupashup.databinding.OrderItemBinding

class AllOrdersAdapter: RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {
    inner class OrdersViewHolder(private val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBindViewHolder(orders: Order) {
            binding.apply {
                tvOrderId.text = orders.orderId.toString()
                tvOrderDate.text = orders.date
                val resources = itemView.resources
                val colorDrawable = when (getOrderStatus(orders.orderStatus)){
                    is OrderStatus.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Returned -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatus.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val orders = differ.currentList[position]
        holder.onBindViewHolder(orders)
        holder.itemView.setOnClickListener {
            onClick?.invoke(orders)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Order) -> Unit)? = null
}