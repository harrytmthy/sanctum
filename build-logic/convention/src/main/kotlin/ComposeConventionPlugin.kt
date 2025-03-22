
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.harry.sanctum.isApplication
import com.harry.sanctum.isLibrary
import com.harry.sanctum.libs
import com.harry.sanctum.onlyIfTrue
import com.harry.sanctum.relativeToRootProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            when {
                pluginManager.isApplication() -> configureCompose<ApplicationExtension>()
                pluginManager.isLibrary() -> configureCompose<LibraryExtension>()
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.configureCompose() {
        extensions.configure<T> {
            buildFeatures {
                compose = true
            }
            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "androidTestImplementation"(platform(bom))
                "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
            testOptions {
                unitTests {
                    isIncludeAndroidResources = true
                }
            }
        }
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            project.providers.gradleProperty("enableComposeCompilerMetrics")
                .onlyIfTrue(this@configureCompose)
                .relativeToRootProject(this@configureCompose, "compose-metrics")
                .let(metricsDestination::set)

            project.providers.gradleProperty("enableComposeCompilerReports")
                .onlyIfTrue(this@configureCompose)
                .relativeToRootProject(this@configureCompose, "compose-reports")
                .let(reportsDestination::set)

            stabilityConfigurationFiles.add(
                project.provider {
                    project.layout.projectDirectory.file("compose_compiler_config.conf")
                }
            )
        }
    }
}