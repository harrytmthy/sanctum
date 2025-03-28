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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harry.sanctum.core.cryptography.Cryptography
import com.harry.sanctum.core.cryptography.domain.CryptographyRepository
import com.harry.sanctum.core.cryptography.model.CryptographyMetadata
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CryptographyRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CryptographyRepository

    @Before
    fun setup() {
        hiltRule.inject()
        repository.clearStorage()
    }

    @Test
    fun hasMetadata_withoutSavedMetadata_shouldReturnFalse() {
        assertFalse(repository.hasMetadata())
    }

    @Test
    fun hasMetadata_withSavedMetadata_shouldReturnFalse() {
        val metadata = CryptographyMetadata(Cryptography.generateSalt(), "encryptedKey")
        repository.saveMetadata(metadata)

        assertTrue(repository.hasMetadata())
    }

    @Test
    fun loadSessionKey_withCorrectPin_shouldDecryptCorrectly() {
        val pin = "123456".toCharArray()
        repository.generateAndSaveSalt()
        repository.createAndSaveEncryptedSessionKey(pin)

        repository.loadSessionKey(pin)
        val plaintext = "Sanctum is secure"
        val ciphertext = repository.encrypt(plaintext)
        val result = repository.decrypt(ciphertext)

        assertEquals(plaintext, result)
    }

    @Test
    fun encrypt_thenDecrypt_shouldReturnOriginalText() {
        repository.generateAndSaveGuestKey()
        val plaintext = "Sanctum is secure"

        val ciphertext = repository.encrypt(plaintext)
        val result = repository.decrypt(ciphertext)

        assertEquals(plaintext, result)
    }

    @Test
    fun clearStorage_shouldRemoveSessionKeyAndPrefs() {
        val pin = "123456".toCharArray()
        repository.generateAndSaveSalt()
        repository.createAndSaveEncryptedSessionKey(pin)
        repository.loadSessionKey(pin)

        repository.clearStorage()

        assertFailsWith<IllegalStateException> {
            repository.encrypt("Should fail")
        }
    }
}
