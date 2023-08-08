package com.alwihabsyi.shupashup.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alwihabsyi.shupashup.R
import com.alwihabsyi.shupashup.databinding.FragmentIntroductionBinding

class IntroductionFragment: Fragment() {

    private lateinit var binding: FragmentIntroductionBinding

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

        binding.apply {
            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
            }
        }
    }

}