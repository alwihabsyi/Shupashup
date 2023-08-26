package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.order.Order
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _allOrders = MutableLiveData<Resource<List<Order>>>()
    val allOrders: LiveData<Resource<List<Order>>> = _allOrders

    init {
        getAllOrders()
    }

    fun getAllOrders() {
        _allOrders.value = Resource.Loading()
        firestore.collection("user").document(auth.uid!!).collection("orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                _allOrders.value = Resource.Success(orders)
            }
            .addOnFailureListener {
                _allOrders.value = Resource.Error(it.message.toString())
            }
    }
}