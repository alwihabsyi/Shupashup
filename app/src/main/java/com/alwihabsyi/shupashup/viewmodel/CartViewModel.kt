package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.firebase.FirebaseCommon
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.getProductPrice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runInterruptible
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts = MutableLiveData<Resource<List<CartProduct>>>()
    val cartProducts: LiveData<Resource<List<CartProduct>>> = _cartProducts

    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value?.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }

    private val _deleteDialog = MutableLiveData<CartProduct>()
    val deleteDialog: LiveData<CartProduct> = _deleteDialog

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble {
            (it.product.offerPercentage.getProductPrice(it.product.price) * it.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCartProduct()
    }

    private fun getCartProduct() {
        _cartProducts.value = Resource.Loading()
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    _cartProducts.value = Resource.Error(error?.message.toString())
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    _cartProducts.value = Resource.Success(cartProducts)
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.quantityChanging
    ) {
        val index = cartProducts.value?.data?.indexOf(cartProduct)

        // index could be equal to 1 or -1 if the function [getCartProducts] delays which will also
        // delay the result we expect to be inside the [_cartProducts] and to prevent the app from
        // crashing we make a check

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseCommon.quantityChanging.INCREASE -> {
                    _cartProducts.value = Resource.Loading()
                    increaseQuantity(documentId)
                }

                FirebaseCommon.quantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        _deleteDialog.value = cartProduct
                        return
                    }

                    _cartProducts.value = Resource.Loading()
                    decreaseQuantity(documentId)
                }
            }
        }

    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if (exception != null) {
                _cartProducts.value = Resource.Error(exception.message.toString())
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null) {
                _cartProducts.value = Resource.Error(exception.message.toString())
            }
        }
    }

}