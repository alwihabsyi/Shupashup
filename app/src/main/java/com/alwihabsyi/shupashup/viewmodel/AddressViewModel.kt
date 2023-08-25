package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addNewAddress = MutableLiveData<Resource<Address>>()
    val addNewAddress: LiveData<Resource<Address>> = _addNewAddress

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            _addNewAddress.value = Resource.Loading()
            firestore.collection("user").document(auth.uid!!)
                .collection("address").document().set(address)
                .addOnSuccessListener {
                    _addNewAddress.value = Resource.Success(address)
                }
                .addOnFailureListener {
                    _addNewAddress.value = Resource.Error(it.message.toString())
                }
        }else{
            _error.value = "All fields are required"
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}