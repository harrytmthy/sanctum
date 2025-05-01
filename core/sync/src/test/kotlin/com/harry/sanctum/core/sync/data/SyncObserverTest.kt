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

package com.harry.sanctum.core.sync.data

import androidx.core.content.edit
import com.harry.sanctum.core.common.constants.SessionConstants
import com.harry.sanctum.core.cryptography.test.FakeEncryptedPrefsObserver
import com.harry.sanctum.core.database.model.EntrySyncEntity
import com.harry.sanctum.core.database.test.FakeEntrySyncDao
import com.harry.sanctum.core.testing.prefs.FakeSharedPreferences
import com.harry.sanctum.core.testing.rules.MainDispatcherRule
import com.harry.sanctum.core.testing.sync.FakeSyncManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class SyncObserverTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val prefs = FakeSharedPreferences()

    private val prefsObserver = FakeEncryptedPrefsObserver(prefs)

    private val entrySyncDao = FakeEntrySyncDao()

    private val syncManager = FakeSyncManager()

    private val syncObserver = SyncObserverImpl(prefsObserver, entrySyncDao, syncManager)

    @Test
    fun `observeSync should trigger sync twice`() = runTest {
        prefs.edit(commit = true) { putString(SessionConstants.PREF_USER_ID, "123") }
        val job = launch {
            syncObserver.observeSync().collect()
        }

        advanceTimeBy(500L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())
        advanceTimeBy(1600L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())
        advanceTimeBy(2500L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())

        assertEquals(2, syncManager.syncCount)
        job.cancel()
    }

    @Test
    fun `observeSync without userId should not trigger sync`() = runTest {
        val job = launch {
            syncObserver.observeSync().collect()
        }

        advanceTimeBy(500L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())
        advanceTimeBy(1600L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())
        advanceTimeBy(2500L)
        entrySyncDao.upsertPendingEntry(createEntrySyncEntity())

        assertEquals(0, syncManager.syncCount)
        job.cancel()
    }

    private fun createEntrySyncEntity(): EntrySyncEntity =
        EntrySyncEntity(0L, UUID.randomUUID().toString(), Clock.System.now())
}
