/*
 * Copyright 2025 Harry Timothy Tumalewa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harry.sanctum.core.network.handlers

import com.harry.sanctum.core.common.ApiResponse
import com.harry.sanctum.core.common.ApiResponse.ApiError
import com.harry.sanctum.core.common.ApiResponse.NetworkError
import com.harry.sanctum.core.common.ApiResponse.NoInternetError
import com.harry.sanctum.core.common.ApiResponse.Success
import com.harry.sanctum.core.common.ApiResponse.UnknownError
import com.harry.sanctum.core.common.ErrorResponse
import com.harry.sanctum.core.common.exceptions.NoInternetException
import kotlinx.serialization.json.Json
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class ApiResponseCall<T : Any>(private val delegate: Call<T>) : Call<ApiResponse<T>> {

    override fun enqueue(callback: Callback<ApiResponse<T>>) = delegate.enqueue(
        object : Callback<T> {
            override fun onResponse(responseCall: Call<T>, response: Response<T>) {
                val apiResponse = getApiResponse(response)
                callback.onResponse(this@ApiResponseCall, Response.success(apiResponse))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val response = when (t) {
                    is NoInternetException -> NoInternetError
                    is IOException -> NetworkError(t)
                    else -> UnknownError(t)
                }
                callback.onResponse(this@ApiResponseCall, Response.success(response))
            }
        },
    )

    private fun getApiResponse(response: Response<T>): ApiResponse<T> = when {
        response.isSuccessful -> Success(response.body())
        else -> response.errorBody()?.let {
            val errorResponse = Json.decodeFromString<ErrorResponse>(it.string())
            ApiError(errorResponse.error)
        } ?: UnknownError(IllegalStateException("API error with null body"))
    }

    override fun clone(): ApiResponseCall<T> =
        ApiResponseCall(delegate.clone())

    override fun execute(): Response<ApiResponse<T>> =
        Response.success(getApiResponse(delegate.execute()))

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
