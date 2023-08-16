package com.alwihabsyi.shupashup.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.util.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _navigate = MutableLiveData(0)
    val navigate: LiveData<Int> = _navigate

    companion object{
        const val SHOPPING_ACTIVITY = 23
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if(user != null){
            _navigate.value = SHOPPING_ACTIVITY
        }else if(isButtonClicked){
            _navigate.value = ACCOUNT_OPTIONS_FRAGMENT
        }else {
            Unit
        }
    }

    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }

}