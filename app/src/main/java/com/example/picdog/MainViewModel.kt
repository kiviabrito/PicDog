package com.example.picdog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.picdog.model.FeedEntity
import com.example.picdog.ui.main.MainFragment

class MainViewModel : ViewModel() {

  private val _feed = MutableLiveData<ArrayList<String>>()
  val feed: LiveData<ArrayList<String>> = _feed

  fun setIndex(index: Int) {
    when (index) {
      1 -> {
        println("HUSKY")
        _feed.postValue(arrayListOf())
      }
      2 -> {
        println("HOUND")
        _feed.postValue(arrayListOf())
      }
      3 -> {
        println("PUG")
        _feed.postValue(arrayListOf())
      }
      4 -> {
        println("LABRADOR")
        _feed.postValue(arrayListOf())
      }
    }
  }
}