package com.harry.sanctum

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