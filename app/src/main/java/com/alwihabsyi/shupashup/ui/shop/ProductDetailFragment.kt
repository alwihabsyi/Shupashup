package com.alwihabsyi.shupashup.ui.shop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.adapters.ColorsAdapter
import com.alwihabsyi.shupashup.adapters.SizesAdapter
import com.alwihabsyi.shupashup.adapters.ViewPagerImages
import com.alwihabsyi.shupashup.data.CartProduct
import com.alwihabsyi.shupashup.databinding.FragmentProductDetailBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.hideBottomNavigationView
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_alwihabsyi_shupashup_di_AppModule

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailBinding
    private val viewPagerAdapter by lazy { ViewPagerImages() }
    private val sizeAdapter by lazy { SizesAdapter() }
    private val colorAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if(product.colors.isNullOrEmpty()){
                tvColor.hide()
            }

            if(product.sizes.isNullOrEmpty()){
                tvSizes.hide()
            }
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizeAdapter.differ.submitList(it)
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizeAdapter.onItemClick = {
            selectedSize = it
        }

        colorAdapter.onItemClick = {
            selectedColor = it
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        viewModel.addToCart.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.btnAddToCart.startAnimation()
                }
                is Resource.Success -> {
                    binding.btnAddToCart.revertAnimation()
                    binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                }
                is Resource.Error -> {
                    binding.btnAddToCart.stopAnimation()
                    toast(it.message)
                }
            }
        }

    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


}