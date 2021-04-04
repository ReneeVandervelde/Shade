plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(KotlinX.Coroutines.core)
                api(KotlinX.Coroutines.core)
                implementation("com.github.inkapplications.subatomic:core:1.0.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project(":discover"))
                api(project(":lights"))
                api(project(":groups"))
                api(project(":scenes"))
                api(project(":schedules"))
                api(project(":auth"))
            }
        }
    }
}
