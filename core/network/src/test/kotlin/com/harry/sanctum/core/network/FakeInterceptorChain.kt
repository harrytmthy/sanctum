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

package com.harry.sanctum.core.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.Timeout
import java.util.concurrent.TimeUnit

object FakeInterceptorChain : Interceptor.Chain {

    private var connectTimeout: Int = 10000

    private var readTimeout: Int = 10000

    private var writeTimeout: Int = 10000

    val request: Request = Request.Builder()
        .url("http://localhost/")
        .build()

    val response: Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .build()

    override fun call(): Call =
        object : Call {
            override fun cancel() = Unit
            override fun execute(): Response = response
            override fun isCanceled(): Boolean = false
            override fun isExecuted(): Boolean = false
            override fun clone(): Call = this
            override fun enqueue(responseCallback: Callback) = Unit
            override fun request(): Request = request
            override fun timeout(): Timeout = Timeout()
        }

    override fun connection(): Connection? = null

    override fun proceed(request: Request): Response = response

    override fun request(): Request = request

    override fun connectTimeoutMillis(): Int = connectTimeout

    override fun readTimeoutMillis(): Int = readTimeout

    override fun writeTimeoutMillis(): Int = writeTimeout

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain {
        connectTimeout = unit.toMillis(timeout.toLong()).toInt()
        return this
    }

    override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain {
        readTimeout = unit.toMillis(timeout.toLong()).toInt()
        return this
    }

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain {
        writeTimeout = unit.toMillis(timeout.toLong()).toInt()
        return this
    }
}
