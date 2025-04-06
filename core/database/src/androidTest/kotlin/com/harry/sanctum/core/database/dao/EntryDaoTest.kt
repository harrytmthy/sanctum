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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harry.sanctum.core.database.SanctumDatabase
import com.harry.sanctum.core.database.model.EntryEntity
import com.harry.sanctum.core.testing.rules.MainDispatcherRule
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
class EntryDaoTest {

    private lateinit var database: SanctumDatabase

    private lateinit var entryDao: EntryDao

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SanctumDatabase::class.java,
        ).build()
        entryDao = database.entryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getEntries_shouldExcludeDeletedEntries() = runTest {
        val now = Clock.System.now()
        val entries = listOf(
            EntryEntity(0L, UUID.randomUUID().toString(), "Title", "Visible", now, now, null),
            EntryEntity(0L, UUID.randomUUID().toString(), "Deleted", "Gone", now, now, now),
        )
        entryDao.upsertEntries(entries)

        val result = entryDao.getEntries(limit = 20, offset = 0)

        val expected = listOf(entries.first().copy(id = 1L))
        assertEquals(expected, result)
    }

    @Test
    fun getEntriesByIds_shouldReturnOnlyMatchingEntries() = runTest {
        val now = Clock.System.now()
        val entries = listOf(
            EntryEntity(0L, "id1", "Title1", "Content1", now, now, null),
            EntryEntity(0L, "id2", "Title2", "Content2", now, now, null),
            EntryEntity(0L, "id3", "Title3", "Content3", now, now, null),
        )
        entryDao.upsertEntries(entries)

        val result = entryDao.getEntriesByIds(listOf("id1", "id3"))

        val expected = listOf(entries.first().copy(id = 1L), entries.last().copy(id = 3L))
        assertEquals(expected, result)
    }
}
