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

package com.harrytmthy.sanctum.core.cryptography

import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import com.harrytmthy.sanctum.core.cryptography.extensions.toDecodedByteArray
import com.harrytmthy.sanctum.core.cryptography.extensions.toEncodedString
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object Cryptography {

    private const val ALGORITHM = "AES/GCM/NoPadding"

    private const val GCM_IV_SIZE = 12

    private const val GCM_TAG_SIZE = 128

    private const val SALT_SIZE = 16

    fun encrypt(plaintext: String, key: String): String {
        val secretKey = SecretKeySpec(key.toDecodedByteArray(), KEY_ALGORITHM_AES)
        val cipher = Cipher.getInstance(ALGORITHM).apply { init(ENCRYPT_MODE, secretKey) }
        return (cipher.iv + cipher.doFinal(plaintext.toByteArray())).toEncodedString()
    }

    fun decrypt(ciphertext: String, key: String): String {
        val fullBytes = ciphertext.toDecodedByteArray()
        val ivBytes = fullBytes.copyOfRange(0, GCM_IV_SIZE)
        val cipherBytes = fullBytes.copyOfRange(GCM_IV_SIZE, fullBytes.size)
        val spec = GCMParameterSpec(GCM_TAG_SIZE, ivBytes)
        val secretKey = SecretKeySpec(key.toDecodedByteArray(), KEY_ALGORITHM_AES)
        val cipher = Cipher.getInstance(ALGORITHM).apply { init(DECRYPT_MODE, secretKey, spec) }
        return String(cipher.doFinal(cipherBytes))
    }

    fun generateSalt(): String =
        SecureRandom().generateSeed(SALT_SIZE).toEncodedString()
}
