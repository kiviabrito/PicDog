package com.example.picdog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picdog.ui.auth.AuthActivity
import com.example.picdog.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.SplashTheme)
    super.onCreate(savedInstanceState)

    redirect()
  }

  private fun redirect(){
    lifecycleScope.launch(Dispatchers.Default) {
      val user = App.db.userDao().selectAll().firstOrNull()
      if (user == null) {
        val intent = Intent(this@SplashActivity, AuthActivity::class.java)
        startActivity(intent)
        finish()
      } else {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
      }
    }
  }
}