/*
 * Copyright 2025 Harry Timothy Tumalewa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.harrytmthy.sanctum.isApplication
import com.harrytmthy.sanctum.isLibrary
import com.harrytmthy.sanctum.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class KotlinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.isApplication() -> configureAndroid<ApplicationExtension>()
                pluginManager.isLibrary() -> configureAndroid<LibraryExtension>()
                else -> {
                    apply(plugin = "org.jetbrains.kotlin.jvm")
                    configureJvm()
                }
            }
        }
    }

    private inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.configureAndroid() {
        extensions.configure<T> {
            compileSdk = 35
            defaultConfig.minSdk = 24
            when (this) {
                is ApplicationExtension -> defaultConfig.targetSdk = 35
                is LibraryExtension -> defaultConfig.targetSdk = 35
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
                isCoreLibraryDesugaringEnabled = true
            }
        }
        configureKotlin<KotlinAndroidProjectExtension>()
        dependencies {
            "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())
        }
    }

    private fun Project.configureJvm() {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        configureKotlin<KotlinJvmProjectExtension>()
    }

    private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() {
        extensions.configure<T> {
            val warningsAsErrors: String? by project
            when (this) {
                is KotlinAndroidProjectExtension -> compilerOptions
                is KotlinJvmProjectExtension -> compilerOptions
                else -> error("Unsupported project extension $this ${T::class}")
            }.apply {
                jvmTarget = JvmTarget.JVM_11
                allWarningsAsErrors = warningsAsErrors.toBoolean()
                freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
                freeCompilerArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
                freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
            }
        }
    }
}