package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.Product
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableLiveData<Resource<List<Product>>>()
    val specialProducts: LiveData<Resource<List<Product>>> = _specialProducts

    init {
        fetchSpecialProducts()
    }

    fun fetchSpecialProducts() {
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

}