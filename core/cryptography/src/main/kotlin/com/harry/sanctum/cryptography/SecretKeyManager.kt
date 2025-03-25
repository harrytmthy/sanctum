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

package com.harry.sanctum.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import timber.log.Timber
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SecretKeyManager {

    private const val KEY_ALIAS = "SlC2b9Al"

    private const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"

    private const val KEY_DERIVATION_ITERATION_COUNT = 100_000

    private const val KEY_PROVIDER = "AndroidKeyStore"

    private const val KEY_SIZE = 256

    fun getOrCreateSecretKey(): SecretKey? =
        try {
            val keyStore = KeyStore.getInstance(KEY_PROVIDER).apply { load(null) }
            (keyStore.getKey(KEY_ALIAS, null) as? SecretKey) ?: createSecretKey()
        } catch (e: GeneralSecurityException) {
            Timber.e(e)
            null
        }

    fun deriveSecretKeyFromPin(pin: CharArray, salt: ByteArray): SecretKey? =
        try {
            val spec = PBEKeySpec(pin, salt, KEY_DERIVATION_ITERATION_COUNT, KEY_SIZE)
            val key = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM).generateSecret(spec)
            SecretKeySpec(key.encoded, KEY_ALGORITHM_AES)
        } catch (e: GeneralSecurityException) {
            Timber.e(e)
            null
        }

    fun hasSecretKey(): Boolean {
        val keyStore = KeyStore.getInstance(KEY_PROVIDER).apply { load(null) }
        return keyStore.containsAlias(KEY_ALIAS)
    }

    private fun createSecretKey(): SecretKey? =
        try {
            KeyGenerator.getInstance(KEY_ALGORITHM_AES, KEY_PROVIDER).apply {
                init(
                    KeyGenParameterSpec.Builder(KEY_ALIAS, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE_GCM)
                        .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
                        .setKeySize(KEY_SIZE)
                        .setUserAuthenticationRequired(false)
                        .build(),
                )
            }.generateKey()
        } catch (e: GeneralSecurityException) {
            Timber.e(e)
            null
        }
}
