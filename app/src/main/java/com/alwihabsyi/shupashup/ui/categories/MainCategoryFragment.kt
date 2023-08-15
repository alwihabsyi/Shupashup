package com.alwihabsyi.shupashup.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
class MainCategoryFragment: Fragment() {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
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
        viewModel.specialProducts.observe(viewLifecycleOwner){
            when(it) {
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

    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

}