package com.example.picdog.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.picdog.R
import com.example.picdog.R.id.action_sign_out
import com.example.picdog.ui.auth.view.AuthActivity
import com.example.picdog.ui.main.MainStateEvent
import com.example.picdog.ui.main.MainViewModel
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

  lateinit var viewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupView()
  }

  private fun setupView() {
    viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
    val viewPager: ViewPager = findViewById(R.id.view_pager)
    viewPager.adapter = sectionsPagerAdapter
    val tabs: TabLayout = findViewById(R.id.tabs)
    tabs.setupWithViewPager(viewPager)
    val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    subscribeObservers()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    super.onCreateOptionsMenu(menu)
    menuInflater.inflate(R.menu.sign_out, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      action_sign_out -> {
        signOutDialog()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun signOutDialog() {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.sign_out))
      .setMessage(getString(R.string.sign_out_message))
      .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
        dialog.dismiss()
      }
      .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
        viewModel.setStateEvent(MainStateEvent.TappedSignOut)
        dialog.dismiss()
      }
      .show()
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(this, Observer { dataState ->
      // Loading and Message are being handle by the fragment.
      // handle Data<T>
      dataState.data?.let { event ->
        event.getContentIfNotHandled()?.let { mainViewState ->
          println("DEBUG: DataState: $mainViewState")
          mainViewState.isSignOut?.let {
            viewModel.setIsSignOut(it)
          }
        }
      }
    })

    viewModel.viewState.observe(this, Observer { viewState ->
      // Handle Sign Out Response
      viewState.isSignOut?.let { success ->
        println("DEBUG: Sign out response is success: $success")
        handleSignOut()
      }
    })
  }

  private fun handleSignOut() {
    val intent = Intent(this, AuthActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
    startActivity(intent)
    finish()
  }

}