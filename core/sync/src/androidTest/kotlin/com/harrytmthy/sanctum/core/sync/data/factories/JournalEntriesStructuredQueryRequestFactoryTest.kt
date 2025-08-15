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
import com.harrytmthy.sanctum.core.common.factories.StructuredQueryRequestFactory
import com.harrytmthy.sanctum.core.cryptography.di.EncryptedPrefs
import com.harrytmthy.sanctum.core.cryptography.di.EncryptedPrefsModule
import com.harrytmthy.sanctum.core.sync.data.factories.JournalEntriesStructuredQueryRequestFactory.Companion.PAGE_SIZE
import com.harrytmthy.sanctum.core.testing.prefs.FakeSharedPreferences
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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
class JournalEntriesStructuredQueryRequestFactoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @EncryptedPrefs
    @BindValue
    val prefs: SharedPreferences = FakeSharedPreferences()

    @Inject
    lateinit var structuredQueryRequestFactory: StructuredQueryRequestFactory

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun create_withNonNullUserId_shouldCreateStructuredQueryRequest() {
        prefs.edit(commit = true) { putString(PREF_USER_ID, "userId") }
        val page = 3

        val result = structuredQueryRequestFactory.create(page)

        assertEquals(page * PAGE_SIZE, result.structuredQuery.offset)
    }

    @Test
    fun create_withNullUserId_shouldThrowError() {
        prefs.edit(commit = true) { putString(PREF_USER_ID, null) }

        assertFailsWith<IllegalStateException> { structuredQueryRequestFactory.create(1) }
    }
}
