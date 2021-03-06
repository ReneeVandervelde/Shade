plugins {
    application
    kotlin("jvm")
    kotlin("kapt")
}

application {
    applicationName = "shade"
    mainClassName = "inkapplications.shade.cli.MainKt"
}

dependencies {
    implementation(project(":shade"))
    implementation(kotlin("stdlib"))
    implementation(coroutines())
    implementation("com.github.ajalt:clikt:2.1.0")
    implementation("com.google.dagger:dagger:2.25.2")
    kapt("com.google.dagger:dagger-compiler:2.25.2")
}
