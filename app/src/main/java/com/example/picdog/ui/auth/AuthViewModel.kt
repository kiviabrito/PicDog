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

  /**
   * It triggers when _stateEvent is triggered(setStateEvent() method) with a new State Event.
   * Unwrap the StateEvent and if not null, on handleStateEvent(), it handles the Event triggered
   * and emits a new DataState to the view (AuthViewModel).
   */
  val dataState: LiveData<DataState<AuthViewState>> = Transformations
    .switchMap(_stateEvent) { stateEvent ->
      stateEvent?.let {
        handleStateEvent(stateEvent)
      }
    }

  private fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
    println("DEBUG: New StateEvent detected: $stateEvent")
    return when (stateEvent) {

      is AuthStateEvent.SignUpTaped -> {
        repository.getUser(stateEvent.email)
      }

      is AuthStateEvent.None -> {
        AbsentLiveData.create()
      }
    }
  }

  /**
   * Call from the activity when DataState if triggered and updated de ViewState at the Activity.
   */
  fun setUser(isSignUp: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.user = isSignUp
    _viewState.value = update
  }

  private fun getCurrentViewStateOrNew(): AuthViewState {
    return viewState.value?. ?: AuthViewState()
  }

  /**
   * Call when user tap "Sign Up" button, it starts a new state Event.
   */
  fun setStateEvent(event: AuthStateEvent) {
    val state: AuthStateEvent = event
    _stateEvent.value = state
  }

}