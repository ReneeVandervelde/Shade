import org.gradle.kotlin.dsl.DependencyHandlerScope

object KotlinX {
    const val group = "org.jetbrains.kotlinx"

    object Coroutines {
        const val version = "1.4.2"

        const val core = "$group:kotlinx-coroutines-core:$version"
    }
}

object JUnit {
    const val junit = "junit:junit:4.12"
}

@Deprecated("Non-Multiplatform Dependency")
fun DependencyHandlerScope.retrofit(
    module: String = "retrofit",
    version: String = "2.9.0"
) = "com.squareup.retrofit2:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun DependencyHandlerScope.moshi(
    module: String = "moshi",
    version: String = "1.9.3"
) = "com.squareup.moshi:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun DependencyHandlerScope.okHttp(
    module: String = "okhttp",
    version: String = "4.9.0"
) = "com.squareup.okhttp3:$module:$version"

@Deprecated("Non-Multiplatform Dependency")
fun DependencyHandlerScope.threeTen(
    module: String = "threetenbp",
    version: String = "1.5.0",
    includeTimezoneDb: Boolean = false
) = "org.threeten:$module:$version" + (":no-tzdb".takeIf { !includeTimezoneDb }.orEmpty())

@Deprecated("Poorly supported dependency")
fun DependencyHandlerScope.atomicFU(
    module: String = "common",
    version: String = "0.13.2"
) = "org.jetbrains.kotlinx:atomicfu-$module:$version"
