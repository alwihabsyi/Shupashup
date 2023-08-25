package com.alwihabsyi.shupashup.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.adapters.AddressAdapter
import com.alwihabsyi.shupashup.adapters.BillingProductAdapter
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.databinding.FragmentBillingBinding
import com.alwihabsyi.shupashup.util.HorizontalItemDecoration
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.gone
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingFragment: Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val viewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProductsRv()
        setUpAddressRv()

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        viewModel.address.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.progressbarAddress.show()
                }
                is Resource.Success -> {
                    addressAdapter.differ.submitList(it.data)
                    binding.progressbarAddress.gone()
                }
                is Resource.Error -> {
                    binding.progressbarAddress.gone()
                    toast(it.message.toString())
                }
            }
        }

        billingProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "$ $totalPrice"
    }

    private fun setUpBillingProductsRv(){
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpAddressRv(){
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

}