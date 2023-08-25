package com.alwihabsyi.shupashup.data.order

import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)