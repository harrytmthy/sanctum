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

package com.harry.sanctum.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.harry.sanctum.core.database.model.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteUser()

    @Query("DELETE FROM metadata")
    suspend fun deleteMetadata()

    @Query("DELETE FROM entries")
    suspend fun deleteEntries()

    @Query("DELETE FROM entries_sync")
    suspend fun deleteEntriesSync()

    @Transaction
    suspend fun deleteAll() {
        deleteEntriesSync()
        deleteEntries()
        deleteMetadata()
        deleteUser()
    }
}
