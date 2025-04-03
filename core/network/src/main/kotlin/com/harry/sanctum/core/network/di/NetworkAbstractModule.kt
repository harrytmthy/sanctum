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

package com.harry.sanctum.core.network.di

import com.harry.sanctum.core.network.di.qualifiers.ApplicationInterceptor
import com.harry.sanctum.core.network.di.qualifiers.NetworkInterceptor
import com.harry.sanctum.core.network.interceptors.NoInternetInterceptor
import com.harry.sanctum.core.network.utils.ConnectivityManagerNetworkMonitor
import com.harry.sanctum.core.network.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkAbstractModule {

    @ApplicationInterceptor
    @Multibinds
    fun bindApplicationInterceptors(): Set<Interceptor>

    @NetworkInterceptor
    @Multibinds
    fun bindNetworkInterceptors(): Set<Interceptor>

    @ApplicationInterceptor
    @Binds
    fun bindNoInternetInterceptor(interceptor: NoInternetInterceptor): Interceptor

    @Binds
    fun bindNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
