package com.example.picdog.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.picdog.R
import com.example.picdog.R.id.action_sign_out
import com.example.picdog.ui.auth.AuthActivity
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setupView()
  }

  private fun setupView() {
    val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
    val viewPager: ViewPager = findViewById(R.id.view_pager)
    viewPager.adapter = sectionsPagerAdapter
    val tabs: TabLayout = findViewById(R.id.tabs)
    tabs.setupWithViewPager(viewPager)
    val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
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
        dialog.dismiss() }
      .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
        handleSignOut()
        dialog.dismiss()
      }
      .show()
  }

  private fun handleSignOut() {
    val viewModel: MainViewModel by viewModels()
    if (viewModel.signOut()) {
      val intent = Intent(this, AuthActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
      startActivity(intent)
      finish()
    }
  }

}