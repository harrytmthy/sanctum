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

package com.harrytmthy.sanctum.core.sync.di

import com.harrytmthy.sanctum.core.common.factories.BatchWriteRequestFactory
import com.harrytmthy.sanctum.core.common.factories.StructuredQueryRequestFactory
import com.harrytmthy.sanctum.core.common.sync.SyncManager
import com.harrytmthy.sanctum.core.database.model.EntryEntity
import com.harrytmthy.sanctum.core.sync.WorkManagerSyncManager
import com.harrytmthy.sanctum.core.sync.data.EntryPayload
import com.harrytmthy.sanctum.core.sync.data.SyncObserver
import com.harrytmthy.sanctum.core.sync.data.SyncObserverImpl
import com.harrytmthy.sanctum.core.sync.data.factories.JournalEntriesBatchWriteRequestFactory
import com.harrytmthy.sanctum.core.sync.data.factories.JournalEntriesStructuredQueryRequestFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SyncAbstractModule {

    @Binds
    fun bindSyncManager(syncManager: WorkManagerSyncManager): SyncManager

    @Binds
    fun bindJournalEntriesBatchWriteRequestFactory(
        factory: JournalEntriesBatchWriteRequestFactory,
    ): BatchWriteRequestFactory<EntryEntity, EntryPayload>

    @Binds
    fun bindJournalEntriesStructuredQueryRequestFactory(
        factory: JournalEntriesStructuredQueryRequestFactory,
    ): StructuredQueryRequestFactory

    @Binds
    fun bindSyncObserver(observe: SyncObserverImpl): SyncObserver
}
