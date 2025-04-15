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

import android.content.SharedPreferences
import androidx.core.content.edit
import com.harry.sanctum.core.cryptography.Cryptography
import com.harry.sanctum.core.cryptography.KeyHandler
import com.harry.sanctum.core.cryptography.di.EncryptedPrefs
import com.harry.sanctum.core.cryptography.domain.CryptographyRepository
import com.harry.sanctum.core.cryptography.model.CryptographyMetadata
import javax.inject.Inject

internal class CryptographyRepositoryImpl @Inject constructor(
    @EncryptedPrefs private val prefs: SharedPreferences,
) : CryptographyRepository {

    private var sessionKey: String? = null

    override fun hasMetadata(): Boolean =
        prefs.contains(PREF_SALT) && prefs.contains(PREF_ENCRYPTED_SESSION_KEY)

    override fun saveMetadata(metadata: CryptographyMetadata) {
        prefs.edit(commit = true) {
            putString(PREF_SALT, metadata.salt)
            putString(PREF_ENCRYPTED_SESSION_KEY, metadata.encryptedSessionKey)
            if (prefs.contains(PREF_GUEST_KEY)) {
                remove(PREF_GUEST_KEY)
            }
        }
    }

    override fun generateAndSaveGuestKey() {
        prefs.edit(commit = true) { putString(PREF_GUEST_KEY, KeyHandler.generateAesKey()) }
    }

    override fun generateAndSaveSalt() {
        prefs.edit(commit = true) { putString(PREF_SALT, Cryptography.generateSalt()) }
    }

    override fun createAndSaveEncryptedSessionKey(pin: CharArray) {
        val salt = requireNotNull(prefs.getString(PREF_SALT, null))
        val aesKey = KeyHandler.generateAesKey()
        val derivedKey = KeyHandler.deriveSecretKeyFromPin(pin, salt)
        val encryptedSessionKey = Cryptography.encrypt(aesKey, derivedKey)
        prefs.edit(commit = true) {
            putString(PREF_ENCRYPTED_SESSION_KEY, encryptedSessionKey)
            if (prefs.contains(PREF_GUEST_KEY)) {
                remove(PREF_GUEST_KEY)
            }
        }
    }

    override fun loadSessionKey(pin: CharArray) {
        val salt = requireNotNull(prefs.getString(PREF_SALT, null))
        val encryptedSessionKey = requireNotNull(prefs.getString(PREF_ENCRYPTED_SESSION_KEY, null))
        val derivedKey = KeyHandler.deriveSecretKeyFromPin(pin, salt)
        sessionKey = Cryptography.decrypt(encryptedSessionKey, derivedKey)
    }

    override fun encrypt(plaintext: String): String =
        Cryptography.encrypt(plaintext, getSecretKey())

    override fun decrypt(ciphertext: String): String =
        Cryptography.decrypt(ciphertext, getSecretKey())

    private fun getSecretKey(): String =
        sessionKey ?: prefs.getString(PREF_GUEST_KEY, null) ?: error(ERROR_NO_SECRET_KEY)

    override fun clearStorage() {
        prefs.edit(commit = true) { clear() }
        sessionKey = null
    }

    companion object {
        const val PREF_SALT = "cOfXzL63P"
        const val PREF_GUEST_KEY = "Tq1kMno2A"
        const val PREF_ENCRYPTED_SESSION_KEY = "m8gFw7Sj"
        const val ERROR_NO_SECRET_KEY = "Secret key is not found."
    }
}
