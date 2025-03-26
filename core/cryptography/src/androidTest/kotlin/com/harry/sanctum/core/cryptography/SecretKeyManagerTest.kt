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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harry.sanctum.core.cryptography.SecretKeyManager.KEY_ALIAS
import com.harry.sanctum.core.cryptography.SecretKeyManager.KEY_PROVIDER
import com.harry.sanctum.core.cryptography.SecretKeyManager.deriveSecretKeyFromPin
import com.harry.sanctum.core.cryptography.SecretKeyManager.getOrCreateSecretKey
import com.harry.sanctum.core.cryptography.SecretKeyManager.hasSecretKey
import org.junit.runner.RunWith
import java.security.KeyStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SecretKeyManagerTest {

    @Test
    fun getOrCreateSecretKey_onConsecutiveCalls_shouldReturnSameValue() {
        assertEquals(getOrCreateSecretKey(), getOrCreateSecretKey())
    }

    @Test
    fun hasSecretKey_afterSecretKeyCreated_shouldReturnTrue() {
        getOrCreateSecretKey()

        assertTrue(hasSecretKey() == true)
    }

    @Test
    fun hasSecretKey_withCleanEntry_shouldReturnFalse() {
        KeyStore.getInstance(KEY_PROVIDER)
            .apply { load(null) }
            .deleteEntry(KEY_ALIAS)

        assertFalse(hasSecretKey() == true)
    }

    @Test
    fun deriveSecretKeyFromPin_shouldReturnNonEmptyEncodedString() {
        val pin = "123456".toCharArray()
        val salt = byteArrayOf(1)

        val result = deriveSecretKeyFromPin(pin, salt)

        assertTrue(result?.encoded?.isNotEmpty() == true)
    }
}
