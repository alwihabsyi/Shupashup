package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.User
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    init {
        getUser()
    }

    fun getUser() {
        _user.value = Resource.Loading()
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if(error != null) {
                    _user.value = Resource.Error(error.message.toString())
                }else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        _user.value = Resource.Success(user)
                    }
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

}