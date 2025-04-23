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

package com.harry.sanctum.core.sync

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.Test
import kotlin.test.assertEquals

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
internal class SyncWorkerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var syncManager: WorkManagerSyncManager

    @Before
    fun setup() {
        hiltRule.inject()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    /**
     * WorkManager with constraints (e.g., NetworkType.CONNECTED) may fail on API 26 when using
     * SynchronousExecutor during tests. This is a known issue due to the way constraints are
     * simulated and WorkManager's internal threading on lower SDKs.
     *
     * https://issuetracker.google.com/issues/128554485
     */
    @SdkSuppress(minSdkVersion = 27)
    @Test
    fun doWork_shouldSucceedWhenEntriesExist() {
        val request = syncManager.createSyncWorkRequest()
        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
        workManager.enqueue(request).result.get()

        val preRunWorkInfo = workManager.getWorkInfoById(request.id).get()

        assertEquals(WorkInfo.State.ENQUEUED, preRunWorkInfo?.state)

        testDriver.setAllConstraintsMet(request.id)

        val postRequirementWorkInfo = workManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.RUNNING, postRequirementWorkInfo?.state)
    }
}
