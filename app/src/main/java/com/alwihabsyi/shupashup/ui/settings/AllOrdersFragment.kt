package com.alwihabsyi.shupashup.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alwihabsyi.shupashup.adapters.AllOrdersAdapter
import com.alwihabsyi.shupashup.databinding.FragmentOrdersBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.util.toast
import com.alwihabsyi.shupashup.viewmodel.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersFragment: Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val orderAdapter by lazy { AllOrdersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrdersRv()
        observer()

        orderAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setUpOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun observer() {
        viewModel.allOrders.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    binding.progressbarAllOrders.show()
                }
                is Resource.Success -> {
                    binding.progressbarAllOrders.hide()
                    orderAdapter.differ.submitList(it.data)
                    if (it.data.isNullOrEmpty()){
                        binding.tvEmptyOrders.show()
                    }
                }
                is Resource.Error -> {
                    binding.progressbarAllOrders.hide()
                    toast(it.message)
                }
            }
        }
    }

}