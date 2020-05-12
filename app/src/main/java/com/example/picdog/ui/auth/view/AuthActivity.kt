package com.example.picdog.ui.auth.view

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.picdog.R
import com.example.picdog.ui.auth.AuthStateEvent
import com.example.picdog.ui.auth.AuthViewModel
import com.example.picdog.utility.DataState
import com.example.picdog.utility.DataStateListener
import com.example.picdog.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(), DataStateListener {

  lateinit var dataStateHandler: DataStateListener

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)

    backgroundAnimation()
    val viewModel: AuthViewModel by viewModels()
    subscribeObservers(viewModel)
    handleSignUp(viewModel)
  }

  private fun handleSignUp(viewModel: AuthViewModel) {
    sign_up_btn.setOnClickListener {
      val email = email_input.text.toString()
      auth_progress_bar.visibility = View.VISIBLE
      viewModel.setStateEvent(AuthStateEvent.GetUserEvent(email))
    }
  }

  private fun subscribeObservers(viewModel: AuthViewModel) {
    viewModel.dataState.observe(this, Observer { dataState ->

      // Handle Loading and Message
      dataStateHandler.onDataStateChange(dataState)

      // handle Data<T>
      dataState.data?.let { event ->
        event.getContentIfNotHandled()?.let { mainViewState ->

          println("DEBUG: DataState: ${mainViewState}")

          mainViewState.user?.let {
            // set BlogPosts data
            viewModel.setUser(it)
          }
        }
      }
    })

    viewModel.viewState.observe(this, Observer { viewState ->
      viewState.user?.let { response ->
        // set BlogPosts to RecyclerView
        println("DEBUG: Setting blog posts to RecyclerView: ${response}")
        if (response) {
          val intent = Intent(this, MainActivity::class.java)
          startActivity(intent)
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

  override fun onDataStateChange(dataState: DataState<*>?) {
    dataState?.let {
      // Handle loading
      showProgressBar(dataState.loading)

      // Handle Message
      dataState.message?.let { event ->
        event.getContentIfNotHandled()?.let { message ->
          showToast(message)
        }
      }
    }
  }

  private fun showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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