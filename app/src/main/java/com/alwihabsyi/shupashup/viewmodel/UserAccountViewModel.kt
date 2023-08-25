package com.alwihabsyi.shupashup.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alwihabsyi.shupashup.ShupashupApplication
import com.alwihabsyi.shupashup.data.User
import com.alwihabsyi.shupashup.util.RegisterValidation
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    private val _updateInfo = MutableLiveData<Resource<User>>()
    val updateInfo: LiveData<Resource<User>> = _updateInfo

    init {
        getUser()
    }

    fun getUser() {
        _user.value = Resource.Loading()
        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                user?.let {
                    _user.value = Resource.Success(it)
                }
            }
            .addOnFailureListener {
                _user.value = Resource.Error(it.message.toString())
            }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            _updateInfo.value = Resource.Error("Check your inputs")
            return
        }

        _updateInfo.value = Resource.Loading()

        if (imageUri == null) {
            saveUserInfo(user, true)
        } else {
            saveUserInfoWithNewImage(user, imageUri)
        }
    }

    private fun saveUserInfoWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<ShupashupApplication>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInfo(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                _updateInfo.value = Resource.Error(e.message.toString())
            }
        }
    }

    private fun saveUserInfo(user: User, shouldRetrieveoldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrieveoldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser!!.imagePath)
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            _updateInfo.value = Resource.Success(user)
        }.addOnFailureListener {
            _updateInfo.value = Resource.Error("Check your inputs")
        }
    }

}