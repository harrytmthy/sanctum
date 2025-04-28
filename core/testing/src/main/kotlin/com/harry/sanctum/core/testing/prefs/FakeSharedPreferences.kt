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

package com.harry.sanctum.core.testing.prefs

import android.content.SharedPreferences
import com.harry.sanctum.core.testing.prefs.FakeSharedPreferences.Event.Put
import com.harry.sanctum.core.testing.prefs.FakeSharedPreferences.Event.Remove

class FakeSharedPreferences : SharedPreferences {

    private val data = HashMap<String, Any?>()

    private val listeners = HashSet<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): Map<String, *> = data

    override fun getString(key: String, defValue: String?): String? =
        data[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: Set<String?>?): Set<String?>? =
        (data[key] as? Set<String>)?.toSet() ?: defValues

    override fun getInt(key: String, defValue: Int): Int =
        data[key] as? Int ?: defValue

    override fun getLong(key: String, defValue: Long): Long =
        data[key] as? Long ?: defValue

    override fun getFloat(key: String, defValue: Float): Float =
        data[key] as? Float ?: defValue

    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        data[key] as? Boolean ?: defValue

    override fun contains(key: String): Boolean = data.containsKey(key)

    override fun edit(): SharedPreferences.Editor = FakeEditor()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) {
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) {
        listeners.remove(listener)
    }

    inner class FakeEditor : SharedPreferences.Editor {

        private val modifiedData = HashMap<String, Event>()

        override fun putString(key: String, value: String?): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(value) }

        override fun putStringSet(key: String, values: Set<String?>?): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(values?.toSet()) }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(value) }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(value) }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(value) }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
            apply { modifiedData[key] = Put(value) }

        override fun remove(key: String): SharedPreferences.Editor =
            apply { modifiedData[key] = Remove }

        override fun clear(): SharedPreferences.Editor =
            apply { (data.keys + modifiedData.keys).forEach { modifiedData.put(it, Remove) } }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            for ((key, value) in modifiedData) {
                when {
                    value is Put && data[key] != value.data -> data[key] = value.data
                    value is Remove && data.containsKey(key) -> data.remove(key)
                    else -> continue
                }
                listeners.forEach { it.onSharedPreferenceChanged(this@FakeSharedPreferences, key) }
            }
            modifiedData.clear()
        }
    }

    private sealed class Event {
        data class Put(val data: Any?) : Event()
        object Remove : Event()
    }
}
