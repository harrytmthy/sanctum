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

package com.harry.sanctum.core.sync.data.factories

import android.content.SharedPreferences
import com.harry.sanctum.core.common.CollectionSelector
import com.harry.sanctum.core.common.Cursor
import com.harry.sanctum.core.common.FieldReference
import com.harry.sanctum.core.common.Order
import com.harry.sanctum.core.common.StructuredQuery
import com.harry.sanctum.core.common.StructuredQueryRequest
import com.harry.sanctum.core.common.constants.SessionConstants.ERROR_NULL_USER_ID
import com.harry.sanctum.core.common.constants.SessionConstants.PREF_USER_ID
import com.harry.sanctum.core.common.di.EncryptedPrefs
import com.harry.sanctum.core.common.factories.StructuredQueryRequestFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class JournalEntriesStructuredQueryRequestFactory @Inject constructor(
    @EncryptedPrefs private val prefs: SharedPreferences,
) : StructuredQueryRequestFactory {

    override fun create(page: Int): StructuredQueryRequest {
        val userId = prefs.getString(PREF_USER_ID, null) ?: error(ERROR_NULL_USER_ID)
        val structuredQuery = StructuredQuery(
            from = listOf(CollectionSelector("journals/$userId/entries")),
            orderBy = listOf(Order(FieldReference(ORDER_BY_FIELD), direction = ORDER_BY_DIRECTION)),
            offset = page * PAGE_SIZE,
            limit = PAGE_SIZE,
            startAt = Cursor(emptyList(), before = false),
        )
        return StructuredQueryRequest(structuredQuery)
    }

    companion object {
        const val ORDER_BY_DIRECTION = "DESCENDING"
        const val ORDER_BY_FIELD = "updatedAt"
        const val PAGE_SIZE = 20
    }
}
