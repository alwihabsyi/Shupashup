package com.alwihabsyi.shupashup.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.FragmentLoginBinding
import com.alwihabsyi.shupashup.ui.shop.ShoppingActivity
import com.alwihabsyi.shupashup.util.RegisterValidation
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                viewModel.login(email, password)
            }

            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

        }

        viewModel.login.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.btnLogin.startAnimation()
                }
                is Resource.Success -> {
                    binding.btnLogin.revertAnimation()
                    Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                is Resource.Error -> {
                    binding.btnLogin.revertAnimation()
                    toast(it.message.toString())
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