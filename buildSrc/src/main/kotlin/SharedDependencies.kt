import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.coroutines(
    module: String = "core",
    version: String = "1.3.8"
) = "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$version"

fun DependencyHandlerScope.retrofit(
    module: String = "retrofit",
    version: String = "2.9.0"
) = "com.squareup.retrofit2:$module:$version"

fun DependencyHandlerScope.moshi(
    module: String = "moshi",
    version: String = "1.9.3"
) = "com.squareup.moshi:$module:$version"

fun DependencyHandlerScope.okHttp(
    module: String = "okhttp",
    version: String = "4.8.0"
) = "com.squareup.okhttp3:$module:$version"

fun DependencyHandlerScope.threeTen(
    module: String = "threetenbp",
    version: String = "1.4.4",
    includeTimezoneDb: Boolean = false
) = "org.threeten:$module:$version" + (":no-tzdb".takeIf { !includeTimezoneDb }.orEmpty())

fun DependencyHandlerScope.jUnit(
    module: String = "junit",
    version: String = "4.12"
) = "junit:$module:$version"

fun DependencyHandlerScope.atomicFU(
    module: String = "common",
    version: String = "0.13.2"
) = "org.jetbrains.kotlinx:atomicfu-$module:$version"
