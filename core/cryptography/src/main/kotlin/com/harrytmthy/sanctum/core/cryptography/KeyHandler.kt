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
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal object KeyHandler {

    private const val DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"

    private const val DERIVATION_ITERATION_COUNT = 100_000

    private const val KEY_SIZE = 256

    fun generateAesKey(): String =
        KeyGenerator.getInstance(KEY_ALGORITHM_AES)
            .apply { init(KEY_SIZE) }
            .generateKey()
            .encoded
            .toEncodedString()

    fun deriveSecretKeyFromPin(pin: CharArray, salt: String): String {
        val spec = PBEKeySpec(pin, salt.toDecodedByteArray(), DERIVATION_ITERATION_COUNT, KEY_SIZE)
        return SecretKeyFactory.getInstance(DERIVATION_ALGORITHM)
            .generateSecret(spec)
            .encoded
            .toEncodedString()
    }
}
