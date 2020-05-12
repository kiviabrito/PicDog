package com.example.picdog.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Chain): Response {
    if (!isConnected) {
      throw NoConnectivityException()
    }
    val builder: Request.Builder = chain.request().newBuilder()
    return chain.proceed(builder.build())
  }

  private val isConnected: Boolean
    get() {
      val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      } else {
        val netInfo = connectivityManager.activeNetworkInfo
        netInfo != null && netInfo.isConnected
      }
    }

}

class NoConnectivityException : IOException() {
  companion object {
    const val MESSAGE = "Internet Connection"
  }
  override val message: String
    get() = MESSAGE
}