plugins {
    kotlin("multiplatform")
    serialization
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(KotlinX.Serialization.json)
                api(KotlinX.DateTime.core)
                // TODO: Only used for formatting, since KotlinX datetime has none.
                implementation("com.soywiz.korlibs.klock:klock:2.0.7")
                implementation(Ktor.client)
                api("com.github.ajalt.colormath:colormath:2.0.0")
                api("com.github.inkapplications.spondee:math:7144fd679a")

                implementation(KotlinX.Serialization.json)
                implementation(Ktor.client)
                implementation(Ktor.clientSerialization)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(JUnit.junit)
                implementation(kotlin("test"))
            }
        }
    }
}
