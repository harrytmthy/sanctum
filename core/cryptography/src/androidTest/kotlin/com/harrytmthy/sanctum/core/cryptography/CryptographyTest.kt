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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harrytmthy.sanctum.core.cryptography.extensions.toEncodedString
import org.junit.runner.RunWith
import java.security.GeneralSecurityException
import javax.crypto.KeyGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class CryptographyTest {

    @Test
    fun decrypt_withSecretKey_shouldTransformCiphertextToPlaintext() {
        val plaintext = "some plaintext"
        val key = KeyHandler.generateAesKey()
        val ciphertext = Cryptography.encrypt(plaintext, key)

        val result = Cryptography.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withDerivedKey_shouldTransformCiphertextToPlaintext() {
        val plaintext = "some plaintext"
        val pin = "123456".toCharArray()
        val key = KeyHandler.deriveSecretKeyFromPin(pin, Cryptography.generateSalt())
        val ciphertext = Cryptography.encrypt(plaintext, key)

        val result = Cryptography.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withWrongKey_shouldThrowException() {
        val plaintext = "some plaintext"
        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES).apply { init(256) }
        val firstKey = keyGenerator.generateKey().encoded.toEncodedString()
        val secondKey = keyGenerator.generateKey().encoded.toEncodedString()
        val ciphertext = Cryptography.encrypt(plaintext, firstKey)

        assertFailsWith<GeneralSecurityException> {
            Cryptography.decrypt(ciphertext, secondKey)
        }
    }

    @Test
    fun decrypt_withMalformedCipherText_shouldThrowException() {
        val plaintext = "some plaintext"
        val key = KeyHandler.generateAesKey()
        val ciphertext = Cryptography.encrypt(plaintext, key)

        assertFailsWith<GeneralSecurityException> {
            Cryptography.decrypt(ciphertext.substring(1, ciphertext.length), key)
        }
    }

    @Test
    fun decrypt_withEmptyPlainText_shouldTransformCiphertextToPlaintext() {
        val plaintext = ""
        val key = KeyHandler.generateAesKey()
        val ciphertext = Cryptography.encrypt(plaintext, key)

        val result = Cryptography.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }

    @Test
    fun decrypt_withEmoji_shouldTransformCiphertextToPlaintext() {
        val plaintext = """
            我的名字是 Harry 😇
            よろしくお願いします!
        """.trimIndent()
        val key = KeyHandler.generateAesKey()
        val ciphertext = Cryptography.encrypt(plaintext, key)

        val result = Cryptography.decrypt(ciphertext, key)

        assertEquals(plaintext, result)
    }
}
