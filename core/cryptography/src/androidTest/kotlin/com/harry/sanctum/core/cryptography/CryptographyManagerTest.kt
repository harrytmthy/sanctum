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

import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import javax.crypto.KeyGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class CryptographyManagerTest {

    @Test
    fun decrypt_withSecretKey_shouldTransformCiphertextToPlaintext() {
        val plaintext = "some plaintext"
        val key = SecretKeyManager.getOrCreateSecretKey()
        requireNotNull(key)
        val ciphertext = CryptographyManager.encrypt(plaintext, key)
        requireNotNull(ciphertext)
        val result = CryptographyManager.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withDerivedKey_shouldTransformCiphertextToPlaintext() {
        val plaintext = "some plaintext"
        val key = SecretKeyManager.deriveSecretKeyFromPin("123456".toCharArray(), byteArrayOf(1))
        requireNotNull(key)
        val ciphertext = CryptographyManager.encrypt(plaintext, key)
        requireNotNull(ciphertext)

        val result = CryptographyManager.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withWrongKey_shouldReturnNull() {
        val plaintext = "some plaintext"
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES).apply { init(256) }
        val ciphertext = CryptographyManager.encrypt(plaintext, keyGenerator.generateKey())
        requireNotNull(ciphertext)

        val result = CryptographyManager.decrypt(ciphertext, keyGenerator.generateKey())

        assertNull(result)
    }

    @Test
    fun decrypt_withMalformedCipherText_shouldReturnNull() {
        val plaintext = "some plaintext"
        val key = SecretKeyManager.getOrCreateSecretKey()
        requireNotNull(key)
        val ciphertext = CryptographyManager.encrypt(plaintext, key)
        requireNotNull(ciphertext)

        val result = CryptographyManager.decrypt(ciphertext.substring(1, ciphertext.length), key)

        assertNull(result)
    }

    @Test
    fun decrypt_withEmptyPlainText_shouldTransformCiphertextToPlaintext() {
        val plaintext = ""
        val key = SecretKeyManager.getOrCreateSecretKey()
        requireNotNull(key)
        val ciphertext = CryptographyManager.encrypt(plaintext, key)
        requireNotNull(ciphertext)
        val result = CryptographyManager.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withEmoji_shouldTransformCiphertextToPlaintext() {
        val plaintext = """
            我的名字是 Harry 😇
            よろしくお願いします!
        """.trimIndent()
        val key = SecretKeyManager.getOrCreateSecretKey()
        requireNotNull(key)
        val ciphertext = CryptographyManager.encrypt(plaintext, key)
        requireNotNull(ciphertext)
        val result = CryptographyManager.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }
}
