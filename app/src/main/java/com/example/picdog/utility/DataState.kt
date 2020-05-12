package com.example.picdog.utility

/**
 * Class used to send updates to the View, it could call the methods:
 *  - error, which sets the message var,
 *  - loading, which sets the loading var or
 *  - data, which sets the message and data var.
 */

data class DataState<T>(
  var message: Event<String>? = null,
  var loading: Boolean = false,
  var data: Event<T>? = null
) {
  companion object {

    fun <T> error(
      message: String
    ): DataState<T> {
      return DataState(
        message = Event(message),
        loading = false,
        data = null
      )
    }

    fun <T> loading(
      isLoading: Boolean
    ): DataState<T> {
      return DataState(
        message = null,
        loading = isLoading,
        data = null
      )
    }

    fun <T> data(
      message: String? = null,
      data: T? = null
    ): DataState<T> {
      return DataState(
        message = Event.messageEvent(message),
        loading = false,
        data = Event.dataEvent(data)
      )
    }
  }

  override fun toString(): String {
    return "DataState(message=$message,loading=$loading,data=$data)"
  }
}