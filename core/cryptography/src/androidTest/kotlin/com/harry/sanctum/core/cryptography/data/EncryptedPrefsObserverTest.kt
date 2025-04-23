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

package com.harry.sanctum.core.cryptography.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harry.sanctum.core.cryptography.di.EncryptedPrefs
import com.harry.sanctum.core.testing.coroutines.TestDispatchersProvider
import com.harry.sanctum.core.testing.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.Test
import kotlin.test.assertEquals

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EncryptedPrefsObserverTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @EncryptedPrefs
    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var observer: EncryptedPrefsObserver

    @Before
    fun setup() {
        hiltRule.inject()
        observer = EncryptedPrefsObserverImpl(prefs, TestDispatchersProvider)
        prefs.edit(commit = true) { clear() }
    }

    @Test
    fun observeKey_shouldEmitOnChange_andRespectDistinctUntilChanged() = runTest {
        val key = "testKey"
        val emissions = mutableListOf<String?>()
        val job = launch(TestDispatchersProvider.io) {
            observer.observe<String>(key).toList(emissions)
        }

        prefs.edit(commit = true) { putString(key, "Test") }
        prefs.edit(commit = true) { putString(key, "Test") }
        prefs.edit(commit = true) { putString(key, "Some Test") }

        assertEquals(listOf(null, "Test", "Some Test"), emissions.takeLast(3))
        job.cancel()
    }
}
