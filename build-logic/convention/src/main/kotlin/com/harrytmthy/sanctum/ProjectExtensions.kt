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

package com.harrytmthy.sanctum

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun PluginManager.isApplication(): Boolean = hasPlugin("com.android.application")

fun PluginManager.isLibrary(): Boolean = hasPlugin("com.android.library")

fun Provider<String>.onlyIfTrue(project: Project) = flatMap {
    project.provider { it.takeIf(String::toBoolean) }
}

@Suppress("UnstableApiUsage")
fun Provider<*>.relativeToRootProject(project: Project, dir: String) = with(project) {
    map {
        isolated.rootProject.projectDirectory
            .dir("build")
            .dir(projectDir.toRelativeString(rootDir))
    }.map { it.dir(dir) }
}