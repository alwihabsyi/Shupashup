package com.alwihabsyi.shupashup.ui.shop

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.FragmentProfileBinding
import com.alwihabsyi.shupashup.ui.auth.AuthActivity
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.gone
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.showBottomNavigationView
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.ProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray(), false)
            findNavController().navigate(action)
        }
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.tvVersion.text = "Version 1"
    }
    @SuppressLint("SetTextI18n")
    private fun observer() {
        viewModel.user.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Loading -> {
                    binding.progressbarSettings.show()
                }
                is Resource.Success -> {
                    binding.progressbarSettings.gone()
                    Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(Color.BLACK))
                        .into(binding.imageUser)
                    binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                }
                is Resource.Error -> {
                    toast(it.message)
                    binding.progressbarSettings.gone()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}