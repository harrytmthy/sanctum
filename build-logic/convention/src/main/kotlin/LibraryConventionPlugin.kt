import com.android.build.gradle.LibraryExtension
import com.harry.sanctum.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class LibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "convention.kotlin")
            extensions.configure<LibraryExtension> {
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true
                // Applies "core_module1_" prefix to the resources for ":core:module1"
                resourcePrefix = path.split("""\W""".toRegex()).drop(1).distinct()
                    .joinToString(separator = "_")
                    .lowercase() + "_"
            }
            dependencies {
                "testImplementation"(libs.findLibrary("kotlin.test").get())
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
            }
        }
    }
}
