package com.test.contactsexample.introduction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.test.contactsexample.R
import com.test.contactsexample.databinding.FragmentIntroductionBinding
import com.test.contactsexample.utils.autoCleared

/**
 * Created by Marko
 */
class IntroductionFragment : Fragment() {

    private var binding: FragmentIntroductionBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)

        binding.continueBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introduction_to_location)
        }

        return binding.root
    }
}