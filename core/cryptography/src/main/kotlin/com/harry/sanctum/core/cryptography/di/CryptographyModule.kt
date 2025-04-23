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

package com.harry.sanctum.core.cryptography.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.EncryptedSharedPreferences.create
import androidx.security.crypto.MasterKey
import com.harry.sanctum.core.cryptography.data.CryptographyRepositoryImpl
import com.harry.sanctum.core.cryptography.data.EncryptedPrefsObserver
import com.harry.sanctum.core.cryptography.data.EncryptedPrefsObserverImpl
import com.harry.sanctum.core.cryptography.domain.CryptographyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CryptographyModule {

    private const val PREF_FILE_NAME = "SDE7Bx0C"

    @EncryptedPrefs
    @Singleton
    @Provides
    fun provideEncryptedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return create(context, PREF_FILE_NAME, masterKey, AES256_SIV, AES256_GCM)
    }

    @Singleton
    @Provides
    fun provideRepository(repository: CryptographyRepositoryImpl): CryptographyRepository =
        repository

    @Singleton
    @Provides
    fun provideEncryptedPrefsObserver(impl: EncryptedPrefsObserverImpl): EncryptedPrefsObserver =
        impl
}
