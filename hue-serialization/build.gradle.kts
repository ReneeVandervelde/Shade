plugins {
    kotlin("jvm")
    kotlin("kapt")
}

publishJava()

dependencies {
    compile(project(":hue-constructs"))

    compile(kotlin("stdlib"))
    compile(KotlinX.Coroutines.core)

    compile(moshi())
    kapt(moshi("moshi-kotlin-codegen"))
    compile(retrofit())
    compile(retrofit("converter-moshi"))

    implementation(threeTen())

    testImplementation(JUnit.junit)
}
