package com.fitriadyaa.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.data.Result
import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.databinding.FragmentLoginBinding
import com.fitriadyaa.storyapp.utils.Preference
import com.fitriadyaa.storyapp.utils.ViewModelFactory
import android.animation.ObjectAnimator

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateViews()

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()

            // Check if any of the fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.warning_fill), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            loginViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success<LoginResponse> -> {
                        processLogin(result.data)
                        showLoading(false)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }

        }

        val isFromRegister: Boolean? = arguments?.getBoolean("is_form_register")
        if (isFromRegister == true) {
            onBackPressed()
        }
    }

    private fun animateViews() {
        val titleWelcome = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val passEdit = ObjectAnimator.ofFloat(binding.passEditText, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val together = AnimatorSet().apply {
            playTogether(titleWelcome, emailEdit, passEdit, btnLogin, tvRegister)
        }

        val loadingVisibilityAnimation = ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val togetherWithLoading = AnimatorSet().apply {
            playSequentially(together, loadingVisibilityAnimation)
        }

        AnimatorSet().apply {
            playSequentially(togetherWithLoading)
            start()
        }
    }



    private fun processLogin(data: LoginResponse) {
        if (data.error) {
            Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
        } else {
            Preference.saveToken(data.loginResult.token, requireContext())
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
            requireActivity().finish()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        binding.emailEditText.isEnabled = !state
        binding.passEditText.isEnabled = !state
        binding.btnLogin.isEnabled = !state
        binding.tvLogin.isEnabled = !state
        binding.tvRegister.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
