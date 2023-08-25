package com.alwihabsyi.shupashup.ui.shop

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.adapters.CartProductAdapter
import com.alwihabsyi.shupashup.databinding.FragmentCartBinding
import com.alwihabsyi.shupashup.firebase.FirebaseCommon
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.VerticalItemDecoration
import com.alwihabsyi.shupashup.util.gone
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        var totalPrice = 0f
        viewModel.productsPrice.observe(viewLifecycleOwner) {
            it?.let {
                totalPrice = it
                binding.tvTotalPrice.text = "$ $it"
            }
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailFragment, b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.quantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.quantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice,
                cartAdapter.differ.currentList.toTypedArray()
            )
            findNavController().navigate(action)
        }

        viewModel.deleteDialog.observe(viewLifecycleOwner) {
            val alertDialog = AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete item from cart")
                setMessage("Do you want to delete this item from your cart?")
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteCartProduct(it)
                    dialog.dismiss()
                }
            }
            alertDialog.create().show()
        }

        viewModel.cartProducts.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressbarCart.show()
                }

                is Resource.Success -> {
                    binding.progressbarCart.hide()
                    if (it.data!!.isEmpty()) {
                        showEmptyCart()
                    } else {
                        hideEmptyCart()
                        cartAdapter.differ.submitList(it.data)
                    }
                }

                is Resource.Error -> {
                    binding.progressbarCart.hide()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.gone()
            rvCart.show()
            totalBoxContainer.show()
            buttonCheckout.show()
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.show()
            rvCart.gone()
            totalBoxContainer.gone()
            buttonCheckout.gone()
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}