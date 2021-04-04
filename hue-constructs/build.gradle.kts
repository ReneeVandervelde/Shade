plugins {
    kotlin("jvm")
    kotlin("kapt")
    serialization
}

dependencies {
    implementation(KotlinX.Serialization.json)
    api(KotlinX.DateTime.core)

    // TODO: Only used for formatting, since KotlinX datetime has none.
    implementation("com.soywiz.korlibs.klock:klock:2.0.7")
    implementation(Ktor.client)

    implementation(moshi())
    kapt(moshi("moshi-kotlin-codegen"))
    api("com.github.ajalt.colormath:colormath:2.0.0")
    api(threeTen())

    testImplementation(JUnit.junit)
    testImplementation(kotlin("test"))
}
