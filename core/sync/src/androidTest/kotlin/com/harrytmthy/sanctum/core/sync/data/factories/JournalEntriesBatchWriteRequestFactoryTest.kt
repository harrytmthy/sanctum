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

package com.harrytmthy.sanctum.core.sync.data.factories

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harrytmthy.sanctum.core.common.constants.SessionConstants.PREF_USER_ID
import com.harrytmthy.sanctum.core.common.factories.BatchWriteRequestFactory
import com.harrytmthy.sanctum.core.cryptography.di.EncryptedPrefs
import com.harrytmthy.sanctum.core.cryptography.di.EncryptedPrefsModule
import com.harrytmthy.sanctum.core.database.model.EntryEntity
import com.harrytmthy.sanctum.core.sync.data.EntryPayload
import com.harrytmthy.sanctum.core.testing.prefs.FakeSharedPreferences
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@UninstallModules(EncryptedPrefsModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class JournalEntriesBatchWriteRequestFactoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @EncryptedPrefs
    @BindValue
    val prefs: SharedPreferences = FakeSharedPreferences()

    @Inject
    lateinit var batchWriteRequestFactory: BatchWriteRequestFactory<EntryEntity, EntryPayload>

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun create_withNonNullUserId_shouldCreateBatchWriteRequest() {
        prefs.edit(commit = true) { putString(PREF_USER_ID, "userId") }

        val request = batchWriteRequestFactory.create(
            listOf(
                EntryEntity(
                    id = 0L,
                    entryId = "entry123",
                    title = "Test Title",
                    content = "Test Content",
                    createdAt = Instant.fromEpochMilliseconds(1710000000000),
                    updatedAt = Instant.fromEpochMilliseconds(1710000010000),
                    deletedAt = null,
                ),
            ),
        )

        val field = request.writes.first().update.fields
        assertEquals("entry123", field.entryId)
        assertEquals("Test Title", field.title)
        assertEquals("Test Content", field.content)
        assertEquals(1710000000000, field.createdAt)
        assertEquals(1710000010000, field.updatedAt)
        assertEquals(null, field.deletedAt)
    }

    @Test
    fun create_withNullUserId_shouldThrowError() {
        prefs.edit(commit = true) { putString(PREF_USER_ID, null) }

        assertFailsWith<IllegalStateException> { batchWriteRequestFactory.create(emptyList()) }
    }
}
