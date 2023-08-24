package com.alwihabsyi.shupashup.ui.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.ActivityShoppingBinding
import com.alwihabsyi.shupashup.util.Resource
import com.alwihabsyi.shupashup.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shopHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        viewModel.cartProducts.observe(this){
            when(it){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val count = it.data?.size ?: 0
                    val bottomNavigation = binding.bottomNavigation
                    bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                        number = count
                        backgroundColor = resources.getColor(R.color.g_blue)
                    }
                }
                is Resource.Error -> TODO()
            }
        }

    }
}