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

package com.harrytmthy.sanctum.core.sync.data.sources

import com.harrytmthy.sanctum.core.common.BatchWriteRequest
import com.harrytmthy.sanctum.core.common.BatchWriteResponse
import com.harrytmthy.sanctum.core.common.DocumentWrapper
import com.harrytmthy.sanctum.core.common.StructuredQueryRequest
import com.harrytmthy.sanctum.core.sync.data.EntryPayload
import com.harrytmthy.sanctum.core.sync.data.api.JournalEntriesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class JournalEntriesDataSourceImpl @Inject constructor(
    private val journalEntriesApi: JournalEntriesApi,
) : JournalEntriesDataSource {

    override suspend fun getEntries(
        request: StructuredQueryRequest,
    ): List<DocumentWrapper<EntryPayload>>? = journalEntriesApi.getJournalEntries(request).unwrap()

    override suspend fun upsertEntries(
        request: BatchWriteRequest<EntryPayload>,
    ): BatchWriteResponse? = journalEntriesApi.upsertJournalEntries(request).unwrap()
}
