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

package com.harrytmthy.sanctum.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.harrytmthy.sanctum.core.database.converters.InstantConverter
import com.harrytmthy.sanctum.core.database.dao.EntryDao
import com.harrytmthy.sanctum.core.database.dao.EntrySyncDao
import com.harrytmthy.sanctum.core.database.dao.MetadataDao
import com.harrytmthy.sanctum.core.database.dao.UserDao
import com.harrytmthy.sanctum.core.database.model.EntryEntity
import com.harrytmthy.sanctum.core.database.model.EntrySyncEntity
import com.harrytmthy.sanctum.core.database.model.MetadataEntity
import com.harrytmthy.sanctum.core.database.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        MetadataEntity::class,
        EntryEntity::class,
        EntrySyncEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class SanctumDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun metadataDao(): MetadataDao
    abstract fun entryDao(): EntryDao
    abstract fun entrySyncDao(): EntrySyncDao
}
