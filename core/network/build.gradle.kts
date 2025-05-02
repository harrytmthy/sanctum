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
import java.util.Properties

plugins {
    alias(libs.plugins.convention.library)
    alias(libs.plugins.convention.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.harrytmthy.sanctum.core.network"
    defaultConfig {
        rootProject.file("secrets.properties")
            .reader()
            .use { Properties().apply { load(it) } }
            .getProperty("BACKEND_URL")
            .let { buildConfigField("String", "BACKEND_URL", "$it") }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.common)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.timber)
}