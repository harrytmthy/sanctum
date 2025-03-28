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

package com.harry.sanctum.core.cryptography.domain

import androidx.annotation.WorkerThread
import com.harry.sanctum.core.cryptography.model.CryptographyMetadata

interface CryptographyRepository {

    /**
     * Returns true if local cryptographic metadata (e.g. salt and encrypted session key)
     * has been previously saved. Used to determine session and login state.
     */
    fun hasMetadata(): Boolean

    /**
     * Saves the given [metadata] locally via EncryptedSharedPreferences.
     * Typically called after fetching from backend during login or restore flows.
     */
    @WorkerThread
    fun saveMetadata(metadata: CryptographyMetadata)

    /**
     * Generates a new Guest Key (random AES key) and saves it locally.
     * Used in guest mode for offline encryption.
     */
    @WorkerThread
    fun generateAndSaveGuestKey()

    /**
     * Generates a new cryptographic salt and persists it.
     * Called during initial PIN setup (first-time only).
     */
    @WorkerThread
    fun generateAndSaveSalt()

    /**
     * Creates and stores the Encrypted Session Key.
     *
     * This combines:
     * - A newly generated Session Key (AES)
     * - A Derived Key (from [pin] and salt)
     *
     * The result is stored locally for future decryption.
     */
    @WorkerThread
    fun createAndSaveEncryptedSessionKey(pin: CharArray)

    /**
     * Decrypts the previously stored Encrypted Session Key using a Derived Key
     * (computed from [pin] and salt), then holds it in memory for current session.
     */
    @WorkerThread
    fun loadSessionKey(pin: CharArray)

    /**
     * Encrypts the given [plaintext] using the currently active key (Guest or Session).
     *
     * @throws IllegalStateException if no key is available.
     */
    @WorkerThread
    fun encrypt(plaintext: String): String

    /**
     * Decrypts the given [ciphertext] using the currently active key (Guest or Session).
     *
     * @throws IllegalStateException if no key is available or decryption fails.
     */
    @WorkerThread
    fun decrypt(ciphertext: String): String

    /**
     * Clears all stored cryptographic material:
     * - EncryptedSharedPreferences (salt, keys)
     * - In-memory Session Key (if present)
     */
    @WorkerThread
    fun clearStorage()
}
