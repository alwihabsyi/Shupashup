package com.alwihabsyi.shupashup.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.databinding.FragmentAddressBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment: Fragment() {

    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }
    }

    private fun observer() {
        viewModel.addNewAddress.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.progressbarAddress.show()
                }
                is Resource.Success -> {
                    binding.progressbarAddress.hide()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.progressbarAddress.hide()
                    toast(it.message.toString())
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            toast(it)
        }
    }
}