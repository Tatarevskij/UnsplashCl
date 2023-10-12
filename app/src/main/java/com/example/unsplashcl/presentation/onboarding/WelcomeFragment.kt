package com.example.unsplashcl.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.unsplashcl.ACCESS_TOKEN
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.FragmentWelcomeBinding
import com.example.unsplashcl.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private var mainActivity: WeakReference<MainActivity>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = WeakReference(requireActivity() as MainActivity)
        mainActivity!!.get()!!.binding.buttonPanel.isVisible = false

        if (!viewModel.tokenPrefs.getString(ACCESS_TOKEN, null).isNullOrBlank()){
            findNavController().navigate(R.id.action_global_photosFragment)
        }

        binding.welcomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_authorizationFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}