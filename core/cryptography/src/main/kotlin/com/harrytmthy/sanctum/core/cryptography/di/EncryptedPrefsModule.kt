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

package com.harrytmthy.sanctum.core.cryptography.di

import android.content.Context
import android.content.SharedPreferences
import com.harrytmthy.safebox.SafeBox
import com.harrytmthy.sanctum.core.common.coroutines.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EncryptedPrefsModule {

    private const val PREF_FILE_NAME = "SDE7Bx0C"

    @EncryptedPrefs
    @Singleton
    @Provides
    fun provideEncryptedPreferences(
        @ApplicationContext context: Context,
        dispatchersProvider: DispatchersProvider,
    ): SharedPreferences =
        SafeBox.create(context, fileName = PREF_FILE_NAME, ioDispatcher = dispatchersProvider.io)
}
