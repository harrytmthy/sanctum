import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.harry.sanctum.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("compose") {
            id = libs.plugins.convention.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("feature") {
            id = libs.plugins.convention.feature.get().pluginId
            implementationClass = "FeatureConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.convention.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("kotlin") {
            id = libs.plugins.convention.kotlin.get().pluginId
            implementationClass = "KotlinConventionPlugin"
        }
        register("library") {
            id = libs.plugins.convention.library.get().pluginId
            implementationClass = "LibraryConventionPlugin"
        }
        register("room") {
            id = libs.plugins.convention.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
    }
}