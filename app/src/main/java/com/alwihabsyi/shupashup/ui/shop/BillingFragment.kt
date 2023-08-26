package com.alwihabsyi.shupashup.ui.shop

import android.app.AlertDialog
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
import com.alwihabsyi.shupashup.data.Address
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.data.order.Order
import com.alwihabsyi.shupashup.data.order.OrderStatus
import com.alwihabsyi.shupashup.databinding.FragmentBillingBinding
import com.alwihabsyi.shupashup.util.HorizontalItemDecoration
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.gone
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.BillingViewModel
import com.alwihabsyi.shupashup.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingFragment: Fragment() {

    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

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
        observer()

        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.hide()
                totalBoxContainer.hide()
                middleLine.hide()
                bottomLine.hide()
            }
        }

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        billingProductAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "$ $totalPrice"

        addressAdapter.onClick = {
            selectedAddress = it
            if (!args.payment){
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                toast("Please select an address")
                return@setOnClickListener
            }

            showOrderConfirmationDialog()
        }
    }

    private fun observer() {

        billingViewModel.address.observe(viewLifecycleOwner){
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

        orderViewModel.order.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.buttonPlaceOrder.startAnimation()
                }
                is Resource.Success -> {
                    binding.buttonPlaceOrder.revertAnimation()
                    findNavController().navigateUp()
                    Snackbar.make(requireView(), "Your order was placed", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    binding.buttonPlaceOrder.revertAnimation()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want purchase your order now?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!,
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create().show()
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