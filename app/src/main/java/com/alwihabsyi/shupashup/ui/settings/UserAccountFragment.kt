package com.alwihabsyi.shupashup.ui.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.data.User
import com.alwihabsyi.shupashup.databinding.FragmentCartBinding
import com.alwihabsyi.shupashup.databinding.FragmentUserAccountBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.UserAccountViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment: Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
    }

    private fun observer() {
        viewModel.user.observe(viewLifecycleOwner){
            when(it) {
                is Resource.Loading -> {
                    showUserLoading()
                }
                is Resource.Success -> {
                    hideUserLoading()
                    showUserInformation(it.data!!)
                }
                is Resource.Error -> {
                    toast(it.message)
                }
            }
        }
        viewModel.updateInfo.observe(viewLifecycleOwner){
            when(it) {
                is Resource.Loading -> {
                    binding.buttonSave.startAnimation()
                }
                is Resource.Success -> {
                    binding.buttonSave.revertAnimation()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    toast(it.message)
                }
            }
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.hide()
            imageUser.show()
            imageEdit.show()
            edFirstName.show()
            edLastName.show()
            edEmail.show()
            tvUpdatePassword.show()
            buttonSave.show()
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.show()
            imageUser.hide()
            imageEdit.hide()
            edFirstName.hide()
            edLastName.hide()
            edEmail.hide()
            tvUpdatePassword.hide()
            buttonSave.hide()
        }
    }

}