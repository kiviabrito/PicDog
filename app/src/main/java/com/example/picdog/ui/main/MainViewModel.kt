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

  /**
   * It triggers when _stateEvent is triggered(setStateEvent() method) with a new State Event.
   * Unwrap the StateEvent and if not null, on handleStateEvent(), it handles the Event triggered
   * and emits a new DataState to the view (AuthViewModel).
   */
  val dataState: LiveData<DataState<MainViewState>> = Transformations
    .switchMap(_stateEvent) { stateEvent ->
      stateEvent?.let {
        handleStateEvent(stateEvent)
      }
    }

  private fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
    println("DEBUG: New StateEvent detected: $stateEvent")
    return when (stateEvent) {

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

      is MainStateEvent.TappedSignOut -> {
        repository.signOut()
      }

      is MainStateEvent.None -> {
        return AbsentLiveData.create()
      }
    }
  }

  /**
   * Call from the activity when DataState if triggered and updated de ViewState at the Activity.
   */
  fun setFeedData(list: List<String>) {
    val update = getCurrentViewStateOrNew()
    update.feed = list
    _viewState.value = update
  }

  fun setIsSignOut(isSignOut: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.isSignOut = isSignOut
    _viewState.value = update
  }

  private fun getCurrentViewStateOrNew(): MainViewState {
    return viewState.value ?: MainViewState()
  }

  /**
   * Call when user tap "Sign Out" button or when need fetch data, it starts a new state Event.
   */
  fun setStateEvent(event: MainStateEvent) {
    val state: MainStateEvent = event
    _stateEvent.value = state
  }

}
