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

package com.harry.sanctum.core.cryptography.data

import android.content.Context
import android.content.SharedPreferences
import com.google.crypto.tink.Aead
import com.harry.sanctum.core.cryptography.extensions.toDecodedByteArray
import com.harry.sanctum.core.cryptography.extensions.toEncodedString
import timber.log.Timber

internal class AndroidKeysetPrefs(context: Context, private val aead: Aead) : SharedPreferences {

    private val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    override fun getString(key: String?, defValue: String?): String? {
        val encrypted = prefs.getString(key, null) ?: return defValue
        return try {
            String(aead.decrypt(encrypted.toDecodedByteArray(), key?.toByteArray() ?: ByteArray(0)))
        } catch (e: Exception) {
            Timber.e(e)
            defValue
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return getString(key, null)?.toIntOrNull() ?: defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return getString(key, null)?.toBooleanStrictOrNull() ?: defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return getString(key, null)?.toFloatOrNull() ?: defValue
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return getString(key, null)?.toLongOrNull() ?: defValue
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        val decrypted = getString(key, null) ?: return defValues
        return decrypted.split(",").toMutableSet()
    }

    override fun contains(key: String?): Boolean = prefs.contains(key)

    override fun getAll(): MutableMap<String, *> {
        return prefs.all.mapValues { (key, value) ->
            try {
                String(aead.decrypt((value as String).toDecodedByteArray(), key.toByteArray()))
            } catch (e: Exception) {
                Timber.e(e)
                value
            }
        }.toMutableMap()
    }

    override fun edit(): SharedPreferences.Editor = Editor(prefs.edit(), aead)

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
    ) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    class Editor(
        private val editor: SharedPreferences.Editor,
        private val aead: Aead,
    ) : SharedPreferences.Editor {

        override fun putString(key: String?, value: String?): Editor {
            val encrypted = value?.let {
                aead.encrypt(it.toByteArray(), key?.toByteArray() ?: ByteArray(0)).toEncodedString()
            }
            editor.putString(key, encrypted)
            return this
        }

        override fun putInt(key: String?, value: Int): Editor {
            return putString(key, value.toString())
        }

        override fun putBoolean(key: String?, value: Boolean): Editor {
            return putString(key, value.toString())
        }

        override fun putFloat(key: String?, value: Float): Editor {
            return putString(key, value.toString())
        }

        override fun putLong(key: String?, value: Long): Editor {
            return putString(key, value.toString())
        }

        override fun putStringSet(key: String?, values: MutableSet<String>?): Editor {
            return putString(key, values?.joinToString(","))
        }

        override fun remove(key: String?): Editor {
            editor.remove(key)
            return this
        }

        override fun clear(): Editor {
            editor.clear()
            return this
        }

        override fun commit(): Boolean = editor.commit()

        override fun apply() = editor.apply()
    }

    companion object {
        private const val PREF_FILE_NAME = "SDE7Bx0C"
    }
}
