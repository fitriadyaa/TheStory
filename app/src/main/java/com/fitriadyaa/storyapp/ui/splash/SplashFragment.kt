package com.fitriadyaa.storyapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fitriadyaa.storyapp.R
import com.fitriadyaa.storyapp.utils.Preference

class SplashFragment : Fragment() {

    companion object {
        private const val DURATION: Long = 5000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = Preference.initPref(requireContext(), "onSignIn").getString("token", "")
        val action = if (token != "") {
            SplashFragmentDirections.actionSplashFragmentToMainActivity()
        } else {
            SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(action)
            if (token != "") requireActivity().finish()
        }, DURATION)
    }
}
