package com.alwihabsyi.shupashup.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.FragmentIntroductionBinding
import com.alwihabsyi.shupashup.ui.shop.ShoppingActivity
import com.alwihabsyi.shupashup.viewmodel.IntroductionViewModel
import com.alwihabsyi.shupashup.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.alwihabsyi.shupashup.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment: Fragment() {

    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnContinue.setOnClickListener {
                viewModel.startButtonClick()
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
            }
        }

        viewModel.navigate.observe(viewLifecycleOwner){
            when(it){
                SHOPPING_ACTIVITY -> {
                    Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                ACCOUNT_OPTIONS_FRAGMENT -> {
                    findNavController().navigate(it)
                }

                else -> Unit
            }
        }
    }

}