package com.example.picdog.ui.main

import androidx.lifecycle.*
import com.example.picdog.App
import com.example.picdog.utility.AbsentLiveData
import com.example.picdog.utility.DataState


class MainViewModel(
  private val repository: MainRepository = App.mainRepository
) : ViewModel() {


  private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
  private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

  val viewState: LiveData<MainViewState>
    get() = _viewState


  val dataState: LiveData<DataState<MainViewState>> = Transformations
    .switchMap(_stateEvent){stateEvent ->
      stateEvent?.let {
        handleStateEvent(stateEvent)
      }
    }

  private fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>>{
    println("DEBUG: New StateEvent detected: $stateEvent")
    return when(stateEvent){

      is MainStateEvent.GetFeedEvent -> {
        when (stateEvent.section) {
          1 -> {
            repository.getFeed("husky")
          }
          2 -> {
            repository.getFeed("hound")
          }
          3 -> {
            repository.getFeed("pug")
          }
          4 -> {
            repository.getFeed("labrador")
          }
          else -> AbsentLiveData.create()
        }
      }

      is MainStateEvent.TappedSignOut ->{
        repository.signOut()
      }

      is MainStateEvent.None ->{
        return AbsentLiveData.create()
      }
    }
  }

  fun setFeedData(list: List<String>){
    val update = getCurrentViewStateOrNew()
    update.feed = list
    _viewState.value = update
  }

  fun setIsSignOut(isSignOut : Boolean){
    val update = getCurrentViewStateOrNew()
    update.isSignOut = isSignOut
    _viewState.value = update
  }


  private fun getCurrentViewStateOrNew(): MainViewState {
    return viewState.value?.let {
      it
    }?: MainViewState()
  }

  fun setStateEvent(event: MainStateEvent){
    val state: MainStateEvent = event
    _stateEvent.value = state
  }

}
