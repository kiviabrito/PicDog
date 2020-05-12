package com.example.picdog.ui.auth

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.picdog.R
import com.example.picdog.model.SignupResponse
import com.example.picdog.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

  companion object {
    fun newInstance() = AuthActivity()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)

    backgroundAnimation()
    val viewModel: AuthViewModel by viewModels()
    observer(viewModel)
    handleSignUp(viewModel)
  }

  private fun handleSignUp(viewModel: AuthViewModel) {
    sign_up_btn.setOnClickListener {
      val email = email_input.text.toString()
      auth_progress_bar.visibility = View.VISIBLE
      viewModel.signUp(email)
    }
  }

  private fun observer(viewModel: AuthViewModel) {
    viewModel.signUpResponse.observe(this, Observer { response ->
      auth_progress_bar.visibility = View.GONE
      when (response) {
        is SignupResponse.Success -> {
          val intent = Intent(this, MainActivity::class.java)
          startActivity(intent)
        }
        is SignupResponse.Failure -> {
          Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
        }
      }
    })
  }

  private fun backgroundAnimation() {
    val view = this.findViewById<View>(R.id.activity_auth).rootView
    view.setBackgroundResource(R.drawable.gradual_animation)
    val animationDrawable: AnimationDrawable = view.background as AnimationDrawable
    animationDrawable.setEnterFadeDuration(2500)
    animationDrawable.setExitFadeDuration(5000)
    animationDrawable.start()
  }
}