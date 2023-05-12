plugins {
    kotlin("jvm") version "1.8.21"
}

allprojects {
    group = "io.runebox.mixin"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

dependencies {
    testImplementation(kotlin("test"))
}