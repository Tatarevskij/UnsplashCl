package com.example.unsplashcl.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.unsplashcl.R
import com.example.unsplashcl.authorization.utils.launchAndCollectIn
import com.example.unsplashcl.databinding.FragmentAuthorizationBinding
import com.example.unsplashcl.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!
    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }
    private var mainActivity: WeakReference<MainActivity>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = WeakReference(requireActivity() as MainActivity)
        mainActivity!!.get()!!.binding.buttonPanel.isVisible = false

        binding.loginBtn.setOnClickListener { viewModel.openLoginPage() }

        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            getAuthResponse.launch(it)
        }

        viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_authorizationFragment_to_photosFragment)
        }
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> Toast.makeText(this.requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}