package com.fitriadyaa.storyapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fitriadyaa.storyapp.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()
    }
}
