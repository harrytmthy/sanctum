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

package com.harrytmthy.sanctum.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harrytmthy.sanctum.core.database.SanctumDatabase
import com.harrytmthy.sanctum.core.database.model.EntrySyncEntity
import com.harrytmthy.sanctum.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class EntrySyncDaoTest {

    private lateinit var database: SanctumDatabase

    private lateinit var entrySyncDao: EntrySyncDao

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SanctumDatabase::class.java,
        ).build()
        entrySyncDao = database.entrySyncDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertPendingEntry_shouldInsertAndRetrieveCorrectly() = runTest {
        val now = Clock.System.now()
        val entry = EntrySyncEntity(
            id = 0L,
            entryId = UUID.randomUUID().toString(),
            createdAt = now,
        )

        entrySyncDao.upsertPendingEntry(entry)

        val result = entrySyncDao.observePendingEntries().first()
        assertEquals(1, result.size)
        assertEquals(entry.copy(id = 1L), result.first())
    }

    @Test
    fun deleteSyncedByEntryIds_shouldDeleteCorrectEntryBy() = runTest {
        val now = Clock.System.now()
        val entries = listOf(
            EntrySyncEntity(0L, "id1", now),
            EntrySyncEntity(0L, "id2", now),
            EntrySyncEntity(0L, "id3", now),
        )
        entries.forEach { entrySyncDao.upsertPendingEntry(it) }

        entrySyncDao.deletePendingEntryByIds(listOf("id1", "id2"))

        val result = entrySyncDao.observePendingEntries().first()
        assertEquals(entries.last().copy(id = 3L), result.first())
    }
}
