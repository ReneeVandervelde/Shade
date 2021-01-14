plugins {
    kotlin("jvm")
    kotlin("kapt")
}

publishJava()

dependencies {
    compile(kotlin("stdlib"))
    compile(KotlinX.Coroutines.core)

    implementation(retrofit())
    implementation(retrofit("converter-moshi"))
    implementation(moshi())
    implementation(moshi("moshi-adapters"))
    kapt(moshi("moshi-kotlin-codegen"))

    compile(okHttp())

    testImplementation(JUnit.junit)
}
