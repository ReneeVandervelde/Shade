plugins {
    kotlin("jvm")
    kotlin("kapt")
}

publishJava()

dependencies {
    implementation(project(":hue-serialization"))
    compile(project(":hue-constructs"))

    compile(kotlin("stdlib"))
    compile(KotlinX.Coroutines.core)

    implementation(retrofit())
    implementation(retrofit("converter-moshi"))
    implementation(moshi())
    kapt(moshi("moshi-kotlin-codegen"))

    compile(okHttp())
}
