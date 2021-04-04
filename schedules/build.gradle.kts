plugins {
    kotlin("multiplatform")
    serialization
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(KotlinX.Coroutines.core)

                implementation(KotlinX.Serialization.json)
                implementation(Ktor.client)
                implementation(Ktor.clientSerialization)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project(":hue-constructs"))
                api(project(":auth"))
                api(project(":lights"))
                api(project(":groups"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(JUnit.junit)
            }
        }
    }
}
