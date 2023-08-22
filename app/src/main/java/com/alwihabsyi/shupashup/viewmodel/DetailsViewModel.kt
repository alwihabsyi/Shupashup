package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.firebase.FirebaseCommon
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableLiveData<Resource<CartProduct>>()
    val addToCart: LiveData<Resource<CartProduct>> = _addToCart

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        _addToCart.value = Resource.Loading
        firestore.collection("user")
            .document(auth.uid!!)
            .collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.let {
                    if (it.isEmpty()) { //Add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(cartProduct::class.java)
                        if (product == cartProduct) {//add quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else {
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                _addToCart.value = Resource.Error(it.message.toString())
            }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProduct(cartProduct){ addedProduct, e ->
            if(e == null){
                _addToCart.value = Resource.Success(addedProduct!!)
            }else{
                _addToCart.value = Resource.Error(e.message.toString())
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){ _, e ->
            if(e == null){
                _addToCart.value = Resource.Success(cartProduct)
            }else{
                _addToCart.value = Resource.Error(e.message.toString())
            }
        }
    }

}