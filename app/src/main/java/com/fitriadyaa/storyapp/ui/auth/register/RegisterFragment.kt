package com.fitriadyaa.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.data.Result
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.databinding.FragmentRegisterBinding
import com.fitriadyaa.storyapp.utils.ViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateViews()

        binding.tvLoginHere.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passEditText.text.toString()

            // Check if any of the fields are empty
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.warning_fill), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            registerViewModel.register(name, email, password).observe(viewLifecycleOwner) { result: Result<RegisterResponse> ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        processRegister(result.data)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

    }

    private fun processRegister(data: RegisterResponse) {
        if (data.error) {
            Toast.makeText(requireContext(), getString(R.string.register_error), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.register_success), Toast.LENGTH_LONG).show()
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(isFromRegister = true))
        }
    }

    private fun animateViews() {
        val titleWelcome = ObjectAnimator.ofFloat(binding.tvRegisterHere, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val passEdit = ObjectAnimator.ofFloat(binding.passEditText, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val tvLoginHere = ObjectAnimator.ofFloat(binding.tvLoginHere, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val together = AnimatorSet().apply {
            playTogether(titleWelcome, emailEdit, passEdit, nameEdit, btnRegister, tvLoginHere)
        }

        val loadingVisibilityAnimation = ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 0f, 1f)
            .setDuration(1000)

        val togetherWithLoading = AnimatorSet().apply {
            playTogether(together, loadingVisibilityAnimation)
        }

        AnimatorSet().apply {
            playSequentially(togetherWithLoading)
            start()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
        binding.emailEditText.isEnabled = !state
        binding.passEditText.isEnabled = !state
        binding.btnRegister.isEnabled = !state
        binding.tvLoginHere.isEnabled = !state
        binding.tvLoginHere.isEnabled = !state
        binding.emailEditText.isEnabled = !state
        binding.passEditText.isEnabled = !state
        binding.btnRegister.isEnabled = !state
        binding.tvLoginHere.isEnabled = !state
        binding.tvRegisterHere.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
