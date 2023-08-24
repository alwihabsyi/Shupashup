package com.alwihabsyi.shupashup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.util.RegisterFieldsState
import com.alwihabsyi.shupashup.util.RegisterValidation
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.validateEmail
import com.alwihabsyi.shupashup.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
):ViewModel() {

    private val _login = MutableLiveData<Resource<String>>()
    val login: LiveData<Resource<String>> = _login

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    private val _resetPassword = MutableLiveData<Resource<String>>()
    val resetPassword: LiveData<Resource<String>> = _resetPassword

    fun login(email: String, password: String){
        if(checkValidation(email, password)){
            _login.value = Resource.Loading()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _login.value = Resource.Success("Login success")
                }
                .addOnFailureListener {
                    _login.value = Resource.Error(it.message.toString())
                }
        }else{
            val registerFieldsState = RegisterFieldsState(
                validateEmail(email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }

    fun resetPassword(email: String) {
//        viewModelScope.launch {
//        }
        _resetPassword.value = Resource.Loading()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _resetPassword.value = Resource.Success("The password reset link is sent to your email")
            }
            .addOnFailureListener {
                _resetPassword.value = Resource.Error(it.message.toString())
            }
    }

}