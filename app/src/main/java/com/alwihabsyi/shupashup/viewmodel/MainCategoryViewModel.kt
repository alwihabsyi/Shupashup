package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.Product
import com.alwihabsyi.shupashup.util.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableLiveData<Resource<List<Product>>>()
    val specialProducts: LiveData<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableLiveData<Resource<List<Product>>>()
    val bestDealsProducts: LiveData<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableLiveData<Resource<List<Product>>>()
    val bestProducts: LiveData<Resource<List<Product>>> = _bestProducts

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    private fun fetchSpecialProducts() {
        _specialProducts.value = Resource.Loading
        firestore.collection("Products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { result ->
                val specialProductList = result.toObjects(Product::class.java)
                _specialProducts.value = Resource.Success(specialProductList)
            }
            .addOnFailureListener {
                _specialProducts.value = Resource.Error(it.message.toString())
            }
    }

    private fun fetchBestDealsProducts(){
        _bestDealsProducts.value = Resource.Loading
        firestore.collection("Products").whereEqualTo("category", "Best deals").get()
            .addOnSuccessListener { result ->
                val bestDealsProductsList = result.toObjects(Product::class.java)
                _bestDealsProducts.value = Resource.Success(bestDealsProductsList)
            }
            .addOnFailureListener {
                _bestDealsProducts.value = Resource.Error(it.message.toString())
            }
    }

    private fun fetchBestProducts(){
        _bestProducts.value = Resource.Loading
        firestore.collection("Products").get()
            .addOnSuccessListener { result ->
                val bestProductsList = result.toObjects(Product::class.java)
                _bestProducts.value = Resource.Success(bestProductsList)
            }
            .addOnFailureListener {
                _bestProducts.value = Resource.Error(it.message.toString())
            }
    }

}