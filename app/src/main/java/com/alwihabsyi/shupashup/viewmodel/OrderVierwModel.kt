package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.order.Order
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.internal.aggregatedroot.codegen._com_alwihabsyi_shupashup_ShupashupApplication
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _order = MutableLiveData<Resource<Order>>()
    val order: LiveData<Resource<Order>> = _order

    fun placeOrder(order: Order){
        _order.value = Resource.Loading()
        firestore.runBatch {
            //TODO 1: Add the order into user-orders collection
            //TODO 2: Add the order into orders collection
            //TODO 3: Delete the products from user-cart collection

            //TODO 1:
            firestore.collection("user")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(order)

            //TODO 2:
            firestore.collection("orders").document().set(order)

            //TODO 3:
            firestore.collection("user").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            _order.value = Resource.Success(order)
        }.addOnFailureListener {
            _order.value = Resource.Error(it.message.toString())
        }
    }
}