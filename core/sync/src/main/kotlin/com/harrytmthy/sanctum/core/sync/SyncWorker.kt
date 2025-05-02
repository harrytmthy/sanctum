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

package com.harrytmthy.sanctum.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.harrytmthy.sanctum.core.common.coroutines.DispatchersProvider
import com.harrytmthy.sanctum.core.common.factories.BatchWriteRequestFactory
import com.harrytmthy.sanctum.core.database.dao.EntryDao
import com.harrytmthy.sanctum.core.database.dao.EntrySyncDao
import com.harrytmthy.sanctum.core.database.model.EntryEntity
import com.harrytmthy.sanctum.core.sync.data.EntryPayload
import com.harrytmthy.sanctum.core.sync.data.api.JournalEntriesApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val journalEntriesApi: JournalEntriesApi,
    private val entryDao: EntryDao,
    private val entrySyncDao: EntrySyncDao,
    private val batchWriteRequestFactory: BatchWriteRequestFactory<EntryEntity, EntryPayload>,
    private val dispatchersProvider: DispatchersProvider,
) : CoroutineWorker(applicationContext, workerParams) {

    override suspend fun doWork(): Result =
        withContext(dispatchersProvider.io) {
            val pendingEntries = entryDao.getPendingEntries()
            if (pendingEntries.isEmpty()) {
                return@withContext Result.success()
            }
            try {
                val request = batchWriteRequestFactory.create(pendingEntries)
                val currentEntries = entryDao.getPendingEntries().associateBy { it.entryId }
                journalEntriesApi.upsertJournalEntries(request)
                    .unwrap()
                    ?.status
                    ?.mapIndexedNotNull { index, status ->
                        val pending = pendingEntries[index]
                        currentEntries[pending.entryId]
                            ?.takeIf { status.code == 200 && pending.updatedAt == it.updatedAt }
                            ?.let { pending.entryId }
                    }
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { entrySyncDao.deletePendingEntryByIds(it) }
                Result.success()
            } catch (e: Exception) {
                Timber.e(e)
                Result.failure()
            }
        }
}
