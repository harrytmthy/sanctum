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

package com.harrytmthy.sanctum.core.cryptography.data

import android.content.SharedPreferences
import com.harrytmthy.sanctum.core.common.coroutines.DispatchersProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class PrefsObserverImpl(
    private val prefs: SharedPreferences,
    private val dispatchersProvider: DispatchersProvider,
) : PrefsObserver {

    private val observer = callbackFlow<Pair<String, Any?>> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            key ?: return@OnSharedPreferenceChangeListener
            trySend(key to prefs.all[key])
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> observe(key: String): Flow<T?> =
        observer.filter { it.first == key }
            .map { it.second as? T? }
            .onStart { emit(prefs.all[key] as? T?) }
            .distinctUntilChanged()
            .flowOn(dispatchersProvider.io)
}
