package com.alwihabsyi.shupashup.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.FragmentIntroductionBinding
import com.alwihabsyi.shupashup.ui.shop.ShoppingActivity
import com.google.firebase.auth.FirebaseAuth

class IntroductionFragment: Fragment() {

    private lateinit var binding: FragmentIntroductionBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

}