package com.alwihabsyi.shupashup.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.adapters.BillingProductAdapter
import com.alwihabsyi.shupashup.data.order.Order
import com.alwihabsyi.shupashup.data.order.OrderStatus
import com.alwihabsyi.shupashup.data.order.getOrderStatus
import com.alwihabsyi.shupashup.databinding.FragmentOrderDetailBinding
import com.alwihabsyi.shupashup.util.VerticalItemDecoration

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orders = args.order

        setUpOrderRv()
        binding.apply {
            tvOrderId.text = "Order#${orders.orderId}"
            setUpStepView(orders)
            tvFullName.text = orders.address.fullName
            tvAddress.text = "${orders.address.addressTitle} ${orders.address.city}"
            tvPhoneNumber.text = orders.address.phone
            tvTotalPrice.text = "$ ${orders.totalPrice}"
        }
        billingProductAdapter.differ.submitList(orders.products)
    }

    private fun setUpStepView(orders: Order) {
        binding.apply {
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )
            val currentOrderState = when (getOrderStatus(orders.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }
        }
    }

    private fun setUpOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }

}