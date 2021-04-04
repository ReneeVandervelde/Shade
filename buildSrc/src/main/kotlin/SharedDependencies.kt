import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec

val PluginDependenciesSpec.serialization get() = kotlin("plugin.serialization").version("1.4.10")

object KotlinX {
    const val group = "org.jetbrains.kotlinx"

    object Coroutines: DependencyGroup(
        group = group,
        version = "1.4.2"
    ) {
        val core = dependency("kotlinx-coroutines-core")
    }

    object Serialization: DependencyGroup(
        group = group,
        version = "1.0.1"
    ) {
        val json = dependency("kotlinx-serialization-json")
        val PluginDependenciesSpec.serialization get() = kotlin("plugin.serialization").version("1.4.10")
    }

    object DateTime: DependencyGroup(
        group = group,
        version = "0.1.1"
    ) {
        val core = dependency("kotlinx-datetime")
    }
}

object Ktor: DependencyGroup(
    group = "io.ktor",
    version = "1.5.0"
) {
    val client = dependency("ktor-client-core")
    val clientJson = dependency("ktor-client-json")
    val clientSerialization = dependency("ktor-client-serialization")
    val clientOkHttp = dependency("ktor-client-okhttp")
    val clientJs = dependency("ktor-client-js")
    val clientJsonJs = dependency("ktor-client-json-js")
}

object JUnit: DependencyGroup(
    group = "junit",
    version = "4.12"
) {
    val junit = dependency("junit")
}

@Deprecated("Non-Multiplatform Dependency")
fun retrofit(
    module: String = "retrofit",
    version: String = "2.9.0"
) = "com.squareup.retrofit2:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun moshi(
    module: String = "moshi",
    version: String = "1.11.0"
) = "com.squareup.moshi:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun okHttp(
    module: String = "okhttp",
    version: String = "4.9.0"
) = "com.squareup.okhttp3:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun threeTen(
    module: String = "threetenbp",
    version: String = "1.5.0",
    includeTimezoneDb: Boolean = false
) = "org.threeten:$module:$version" + (":no-tzdb".takeIf { !includeTimezoneDb }.orEmpty())

@Deprecated("Poorly supported dependency")
fun atomicFU(
    module: String = "common",
    version: String = "0.13.2"
) = "org.jetbrains.kotlinx:atomicfu-$module:$version"
