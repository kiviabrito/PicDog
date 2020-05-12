package com.example.picdog.utility


import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : Factory() {
  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (getRawType(returnType) != LiveData::class.java) {
      return null
    }
    val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
    val rawObservableType = getRawType(observableType)
    if (rawObservableType != GenericApiResponse::class.java) {
      throw IllegalArgumentException("Type must be a resource")
    }
    if (observableType !is ParameterizedType) {
      throw IllegalArgumentException("Resource must be parameterized")
    }
    val bodyType = getParameterUpperBound(0, observableType)
    return LiveDataCallAdapter<Any>(bodyType)
  }
}