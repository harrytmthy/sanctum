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

package com.harry.sanctum.core.cryptography

import android.util.Base64
import timber.log.Timber
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptographyManager {

    private const val ALGORITHM = "AES/GCM/NoPadding"

    private const val GCM_IV_SIZE = 12

    private const val GCM_TAG_SIZE = 128

    fun encrypt(plaintext: String, key: SecretKey): String? =
        try {
            val cipher = Cipher.getInstance(ALGORITHM).apply { init(ENCRYPT_MODE, key) }
            (cipher.iv + cipher.doFinal(plaintext.toByteArray())).toEncodedString()
        } catch (e: GeneralSecurityException) {
            Timber.e(e)
            null
        }

    fun decrypt(ciphertext: String, key: SecretKey): String? =
        try {
            val fullBytes = ciphertext.toDecodedByteArray()
            val ivBytes = fullBytes.copyOfRange(0, GCM_IV_SIZE)
            val cipherBytes = fullBytes.copyOfRange(GCM_IV_SIZE, fullBytes.size)
            val spec = GCMParameterSpec(GCM_TAG_SIZE, ivBytes)
            val cipher = Cipher.getInstance(ALGORITHM).apply { init(DECRYPT_MODE, key, spec) }
            String(cipher.doFinal(cipherBytes))
        } catch (e: GeneralSecurityException) {
            Timber.e(e)
            null
        }

    private fun ByteArray.toEncodedString(): String {
        return Base64.encodeToString(this, Base64.DEFAULT)
    }

    private fun String.toDecodedByteArray(): ByteArray {
        return Base64.decode(this, Base64.DEFAULT)
    }
}
