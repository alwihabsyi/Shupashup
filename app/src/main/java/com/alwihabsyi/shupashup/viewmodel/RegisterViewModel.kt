package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.data.User
import com.alwihabsyi.shupashup.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
):ViewModel() {

    private val _register = MutableLiveData<Resource<String>>()
    val register: LiveData<Resource<String>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        _register.value = Resource.Loading
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener {
                it.user?.let {
                    _register.value = Resource.Success("User registered successfully")
                }
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

}