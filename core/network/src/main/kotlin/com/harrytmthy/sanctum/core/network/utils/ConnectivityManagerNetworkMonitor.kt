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

package com.harrytmthy.sanctum.core.network.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest.Builder
import androidx.core.content.getSystemService
import com.harrytmthy.sanctum.core.common.coroutines.DispatchersProvider
import com.harrytmthy.sanctum.core.common.di.ApplicationScope
import com.harrytmthy.sanctum.core.common.extensions.orFalse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    private val dispatchersProvider: DispatchersProvider,
) : NetworkMonitor {

    private val _isOnline = MutableStateFlow(true)
    override val isOnline = _isOnline.asStateFlow()

    private var currentJob: Job? = null

    init {
        startNetworkMonitor()
    }

    private fun startNetworkMonitor() {
        currentJob?.cancel()
        currentJob = applicationScope.launch {
            observeOnlineStatus()
                .catch {
                    Timber.e(it)
                    delay(RETRY_INTERVAL_MILLIS)
                    startNetworkMonitor()
                }
                .collect {
                    _isOnline.update { it }
                }
        }
    }

    private fun observeOnlineStatus(): Flow<Boolean> =
        callbackFlow {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
            if (connectivityManager == null) {
                channel.trySend(false)
                channel.close()
                return@callbackFlow
            }

            val callback = object : NetworkCallback() {

                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks += network
                    channel.trySend(true)
                }

                override fun onLost(network: Network) {
                    networks -= network
                    channel.trySend(networks.isNotEmpty())
                }
            }

            val request = Builder().addCapability(NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, callback)
            connectivityManager.activeNetwork
                ?.let(connectivityManager::getNetworkCapabilities)
                ?.hasCapability(NET_CAPABILITY_INTERNET)
                .orFalse()
                .let(channel::trySend)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
            .flowOn(dispatchersProvider.io)
            .sample(NETWORK_SAMPLING_MILLIS)
            .distinctUntilChanged()

    private companion object {
        const val RETRY_INTERVAL_MILLIS = 1000L
        const val NETWORK_SAMPLING_MILLIS = 500L
    }
}
