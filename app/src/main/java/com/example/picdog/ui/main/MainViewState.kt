package com.example.picdog.ui.main

/**
 * Class with necessary data to update view.
 */
data class MainViewState(
  var feed: List<String>? = null,
  var isSignOut: Boolean? = null
)
