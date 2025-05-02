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

package com.harrytmthy.sanctum.core.network.di

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.harrytmthy.sanctum.core.network.BuildConfig
import com.harrytmthy.sanctum.core.network.BuildConfig.DEBUG
import com.harrytmthy.sanctum.core.network.di.qualifiers.ApplicationInterceptor
import com.harrytmthy.sanctum.core.network.di.qualifiers.BaseUrl
import com.harrytmthy.sanctum.core.network.di.qualifiers.NetworkInterceptor
import com.harrytmthy.sanctum.core.network.handlers.ApiResponseCallAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val CONTENT_TYPE = "application/json"

    private const val TIMEOUT_DURATION = 1L

    @Singleton
    @Provides
    fun provideJsonConverterFactory(): Converter.Factory {
        val json = Json { ignoreUnknownKeys = true }
        return json.asConverterFactory(CONTENT_TYPE.toMediaType())
    }

    @ApplicationInterceptor
    @IntoSet
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            if (DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

    @Singleton
    @Provides
    fun provideCallFactory(
        @ApplicationInterceptor applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
        @NetworkInterceptor networkInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): Call.Factory =
        OkHttpClient()
            .newBuilder()
            .apply {
                applicationInterceptors.forEach(::addInterceptor)
                networkInterceptors.forEach(::addNetworkInterceptor)
            }
            .callTimeout(TIMEOUT_DURATION, TimeUnit.MINUTES)
            .connectTimeout(TIMEOUT_DURATION, TimeUnit.MINUTES)
            .readTimeout(TIMEOUT_DURATION, TimeUnit.MINUTES)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        converterFactory: Converter.Factory,
        callFactory: Lazy<Call.Factory>,
    ): Retrofit =
        Retrofit.Builder()
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .addConverterFactory(converterFactory)
            .baseUrl(BuildConfig.BACKEND_URL)
            .callFactory { callFactory.get().newCall(it) }
            .build()

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        callFactory: Lazy<Call.Factory>,
    ): ImageLoader =
        ImageLoader.Builder(context)
            .callFactory { callFactory.get() }
            .components { add(SvgDecoder.Factory()) }
            .respectCacheHeaders(false)
            .apply {
                if (DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()

    @BaseUrl
    @Singleton
    @Provides
    fun provideBaseUrl(): String = BuildConfig.BACKEND_URL
}
