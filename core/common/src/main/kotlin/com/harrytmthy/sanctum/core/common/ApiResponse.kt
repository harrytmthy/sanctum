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

package com.harrytmthy.sanctum.core.common

import com.harrytmthy.sanctum.core.common.exceptions.ApiErrorException
import com.harrytmthy.sanctum.core.common.exceptions.NoInternetException
import java.io.IOException

/**
 * An API response wrapper which introduces different data states:
 * - [Success] -> The response contains a body which deserialized into [Success.data].
 * - [ApiError] -> Represents non-2xx responses with an error message and status code.
 * - [NetworkError] -> Represents network failures.
 * - [NoInternetError] -> Represent error due to no internet connection.
 * - [UnknownError] -> Represents unexpected exceptions during request or response creation.
 */
sealed class ApiResponse<out T : Any> {

    data class Success<T : Any>(val data: T?) : ApiResponse<T>()

    data class ApiError(val error: ErrorDetail) : ApiResponse<Nothing>()

    data class NetworkError(val error: IOException) : ApiResponse<Nothing>()

    data object NoInternetError : ApiResponse<Nothing>()

    data class UnknownError(val throwable: Throwable) : ApiResponse<Nothing>()

    fun unwrap() = when (this) {
        is Success -> data
        is NoInternetError -> throw NoInternetException
        is ApiError -> throw ApiErrorException(message = error.message, code = error.code)
        is NetworkError -> throw error
        is UnknownError -> throw throwable
    }
}
