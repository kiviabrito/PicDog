package com.example.picdog

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.picdog.ui.auth.AuthActivity
import com.example.picdog.ui.auth.AuthViewModel
import com.example.picdog.ui.main.MainActivity

class SplashActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.SplashTheme)
    super.onCreate(savedInstanceState)

    val viewModel: AuthViewModel by viewModels()
    viewModel.checkIfIsSignUp()

    viewModel.isSignUp.observe(this, Observer { isSignUp ->
      isSignUp?.let { success ->
        if (success) {
          val intent = Intent(this@SplashActivity, MainActivity::class.java)
          startActivity(intent)
          finish()
        } else {
          val intent = Intent(this@SplashActivity, AuthActivity::class.java)
          startActivity(intent)
          finish()
        }
      }
    })
  }

}