package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _address = MutableLiveData<Resource<List<Address>>>()
    val address: LiveData<Resource<List<Address>>> = _address

    init {
        getUserAddresses()
    }

    fun getUserAddresses(){
        _address.value = Resource.Loading()
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if(error != null){
                    _address.value = Resource.Error(error.message.toString())
                    return@addSnapshotListener
                }

                val addresses = value?.toObjects(Address::class.java)
                _address.value = Resource.Success(addresses!!)
            }
    }

}