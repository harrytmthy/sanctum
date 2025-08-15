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

plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.convention.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.harrytmthy.sanctum.core.sync"
    defaultConfig {
        testInstrumentationRunner = "com.harrytmthy.sanctum.core.testing.HiltTestRunner"
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.cryptography)
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.core)
    implementation(libs.timber)

    ksp(libs.hilt.ext.compiler)

    testImplementation(projects.core.cryptographyTest)
    testImplementation(projects.core.databaseTest)
    testImplementation(projects.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.safebox)
}