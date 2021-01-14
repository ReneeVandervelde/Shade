plugins {
    kotlin("jvm")
}

publishJava()

dependencies {
    compile(kotlin("stdlib"))
    compile(okHttp())

    testImplementation(JUnit.junit)
}
