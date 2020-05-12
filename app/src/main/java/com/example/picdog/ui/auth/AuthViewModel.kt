package com.example.picdog.ui.auth

import androidx.lifecycle.*
import com.example.picdog.App
import com.example.picdog.utility.AbsentLiveData
import com.example.picdog.utility.DataState

class AuthViewModel(
  private val repository: AuthRepository = App.authRepository
) : ViewModel() {


  private val _stateEvent: MutableLiveData<AuthStateEvent> = MutableLiveData()
  private val _viewState: MutableLiveData<AuthViewState> = MutableLiveData()

  val viewState: LiveData<AuthViewState>
    get() = _viewState


  val dataState: LiveData<DataState<AuthViewState>> = Transformations
    .switchMap(_stateEvent) { stateEvent ->
      stateEvent?.let {
        handleStateEvent(stateEvent)
      }
    }

  private fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
    println("DEBUG: New StateEvent detected: $stateEvent")
    return when (stateEvent) {

      is AuthStateEvent.GetUserEvent -> {
        repository.getUser(stateEvent.email)
      }

      is AuthStateEvent.None -> {
        AbsentLiveData.create()
      }
    }
  }

  fun setUser(isSignUp: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.user = isSignUp
    _viewState.value = update
  }

  private fun getCurrentViewStateOrNew(): AuthViewState {
    val value = viewState.value?.let {
      it
    } ?: AuthViewState()
    return value
  }

  fun setStateEvent(event: AuthStateEvent) {
    val state: AuthStateEvent = event
    _stateEvent.value = state
  }

}