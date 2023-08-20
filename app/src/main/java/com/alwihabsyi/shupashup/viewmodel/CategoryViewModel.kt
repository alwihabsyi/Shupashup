package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.Category
import com.alwihabsyi.shupashup.data.Product
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offerProduct = MutableLiveData<Resource<List<Product>>>()
    val offerProduct: LiveData<Resource<List<Product>>> = _offerProduct

    private val _bestProducts = MutableLiveData<Resource<List<Product>>>()
    val bestProduct: LiveData<Resource<List<Product>>> = _bestProducts

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        _offerProduct.value = Resource.Loading
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _offerProduct.value  = Resource.Success(products)
            }
            .addOnFailureListener {
                _offerProduct.value = Resource.Error(it.message.toString())
            }
    }
    fun fetchBestProducts() {
        _bestProducts.value = Resource.Loading
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _bestProducts.value  = Resource.Success(products)
            }
            .addOnFailureListener {
                _bestProducts.value = Resource.Error(it.message.toString())
            }
    }

}