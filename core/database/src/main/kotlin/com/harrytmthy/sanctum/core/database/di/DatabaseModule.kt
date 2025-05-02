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

package com.harrytmthy.sanctum.core.database.di

import android.content.Context
import androidx.room.Room
import com.harrytmthy.sanctum.core.database.SanctumDatabase
import com.harrytmthy.sanctum.core.database.dao.EntryDao
import com.harrytmthy.sanctum.core.database.dao.EntrySyncDao
import com.harrytmthy.sanctum.core.database.dao.MetadataDao
import com.harrytmthy.sanctum.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "xK9kLn1zEblHz3t"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SanctumDatabase =
        Room.databaseBuilder(
            context,
            SanctumDatabase::class.java,
            DB_NAME,
        ).build()

    @Singleton
    @Provides
    fun provideUserDao(database: SanctumDatabase): UserDao = database.userDao()

    @Singleton
    @Provides
    fun provideMetadataDao(database: SanctumDatabase): MetadataDao = database.metadataDao()

    @Singleton
    @Provides
    fun provideEntryDao(database: SanctumDatabase): EntryDao = database.entryDao()

    @Singleton
    @Provides
    fun provideEntrySyncDao(database: SanctumDatabase): EntrySyncDao = database.entrySyncDao()
}
