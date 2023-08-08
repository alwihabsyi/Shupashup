package com.alwihabsyi.shupashup.ui.auth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.data.User
import com.alwihabsyi.shupashup.databinding.FragmentLoginBinding
import com.alwihabsyi.shupashup.databinding.FragmentRegisterBinding
import com.alwihabsyi.shupashup.util.RegisterValidation
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    etFirstName.text.toString().trim(),
                    etLastName.text.toString().trim(),
                    etEmail.text.toString().trim()
                )
                val password = etPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }

            tvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        viewModel.register.observe(viewLifecycleOwner){
            when(it) {
                is Resource.Loading -> {
                    binding.btnRegister.startAnimation()
                }
                is Resource.Success -> {
                    toast(it.data.toString())
                    binding.btnRegister.revertAnimation()
                }
                is Resource.Error -> {
                    toast(it.message.toString())
                    binding.btnRegister.revertAnimation()
                }
            }
        }

        lifecycleScope.launch{
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.validation.collect(){ validation ->
                    if (validation.email is RegisterValidation.Failed){
                         withContext(Dispatchers.Main){
                             binding.etEmail.apply {
                                 requestFocus()
                                 error = validation.email.message
                             }
                         }
                    }

                    if (validation.password is RegisterValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.etPassword.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }
    }

}