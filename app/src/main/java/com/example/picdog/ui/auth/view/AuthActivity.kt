package com.example.picdog.ui.auth.view

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.picdog.R
import com.example.picdog.ui.auth.AuthStateEvent
import com.example.picdog.ui.auth.AuthViewModel
import com.example.picdog.ui.main.view.MainActivity
import com.example.picdog.utility.DataState
import com.example.picdog.utility.DataStateListener
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(), DataStateListener {

  lateinit var dataStateHandler: DataStateListener
  lateinit var viewModel: AuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)
    viewModel =  ViewModelProvider(this).get(AuthViewModel::class.java)
    backgroundAnimation()
    subscribeObservers()
    handleSignUp()
  }

  private fun backgroundAnimation() {
    val view = this.findViewById<View>(R.id.activity_auth).rootView
    view.setBackgroundResource(R.drawable.gradual_animation)
    val animationDrawable: AnimationDrawable = view.background as AnimationDrawable
    animationDrawable.setEnterFadeDuration(2500)
    animationDrawable.setExitFadeDuration(5000)
    animationDrawable.start()
  }

  private fun handleSignUp() {
    sign_up_btn.setOnClickListener {
      val email = email_input.text.toString()
      viewModel.setStateEvent(AuthStateEvent.SignUpTaped(email))
    }
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(this, Observer { dataState ->
      // Handle Loading and Message
      dataStateHandler.onDataStateChange(dataState)
      // Handle Data<T>
      dataState.data?.let { event ->
        event.getContentIfNotHandled()?.let { mainViewState ->
          println("DEBUG: DataState: ${mainViewState}")
          mainViewState.user?.let {
            viewModel.setUser(it)
          }
        }
      }
    })

    viewModel.viewState.observe(this, Observer { viewState ->
      // Handle Sign Up Response
      viewState.user?.let { success ->
        println("DEBUG: Sign up response is success: ${success}")
        if (success) {
          val intent = Intent(this, MainActivity::class.java)
          startActivity(intent)
        }
      }

    })
  }

  override fun onDataStateChange(dataState: DataState<*>?) {
    dataState?.let {
      // Handle loading
      showProgressBar(dataState.loading)
      // Handle Message
      dataState.message?.let { event ->
        event.getContentIfNotHandled()?.let { message ->
          Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun showProgressBar(isVisible: Boolean) {
    if (isVisible) {
      auth_progress_bar.visibility = View.VISIBLE
    } else {
      auth_progress_bar.visibility = View.INVISIBLE
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    try {
      dataStateHandler = this
    } catch (e: ClassCastException) {
      println("$this must implement DataStateListener")
    }
  }

}