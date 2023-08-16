package com.alwihabsyi.shupashup.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alwihabsyi.shupashup.adapters.BestDealsAdapter
import com.alwihabsyi.shupashup.adapters.BestProductAdapter
import com.alwihabsyi.shupashup.adapters.SpecialProductsAdapter
import com.alwihabsyi.shupashup.databinding.FragmentMainCategoryBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()
        observer()

    }

    private fun observer() {
        viewModel.specialProducts.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.mainCategoryProgressBar.show()
                }

                is Resource.Success -> {
                    specialProductsAdapter.differ.submitList(it.data)
                    binding.mainCategoryProgressBar.hide()
                }

                is Resource.Error -> {
                    binding.mainCategoryProgressBar.hide()
                    toast(it.message.toString())
                }
            }
        }

        viewModel.bestDealsProducts.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.mainCategoryProgressBar.show()
                }

                is Resource.Success -> {
                    bestDealsAdapter.differ.submitList(it.data)
                    binding.mainCategoryProgressBar.hide()
                }

                is Resource.Error -> {
                    binding.mainCategoryProgressBar.hide()
                    toast(it.message.toString())
                }
            }
        }

        viewModel.bestProducts.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.bestProductProgressBar.show()
                }

                is Resource.Success -> {
                    bestProductsAdapter.differ.submitList(it.data)
                    binding.bestProductProgressBar.hide()
                }

                is Resource.Error -> {
                    binding.bestProductProgressBar.hide()
                    toast(it.message.toString())
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

}