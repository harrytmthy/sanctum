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

package com.harry.sanctum.core.database.test

import com.harry.sanctum.core.database.dao.EntrySyncDao
import com.harry.sanctum.core.database.model.EntrySyncEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop

class FakeEntrySyncDao : EntrySyncDao {

    private val entries = MutableStateFlow(emptyList<EntrySyncEntity>())

    override fun observePendingEntries(): Flow<List<EntrySyncEntity>> =
        entries.asStateFlow().drop(1)

    override suspend fun upsertPendingEntry(entrySync: EntrySyncEntity) {
        entries.emit(listOf(entrySync))
    }

    override suspend fun deletePendingEntryByIds(entryIds: List<String>) {
        val entriesToDelete = entryIds.toHashSet()
        entries.value.toMutableList().removeAll { entriesToDelete.contains(it.entryId) }
    }
}
