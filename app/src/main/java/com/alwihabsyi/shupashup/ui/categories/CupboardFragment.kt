package com.alwihabsyi.shupashup.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.alwihabsyi.shupashup.data.Category
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.util.hide
import com.alwihabsyi.shupashup.util.show
import com.alwihabsyi.shupashup.viewmodel.CategoryViewModel
import com.alwihabsyi.shupashup.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CupboardFragment: BaseCategory() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.offerProduct.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.offerProductsProgressbar.show()
                }
                is Resource.Success -> {
                    offerAdapter.differ.submitList(it.data)
                    binding.offerProductsProgressbar.hide()
                }
                is Resource.Error -> {
                    Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    binding.offerProductsProgressbar.hide()
                }
            }
        }

        viewModel.bestProduct.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    binding.bestProductsProgressbar.show()
                }
                is Resource.Success -> {
                    bestProductsAdapter.differ.submitList(it.data)
                    binding.bestProductsProgressbar.hide()
                }
                is Resource.Error -> {
                    Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    binding.bestProductsProgressbar.hide()
                }
            }
        }
    }

}