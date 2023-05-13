plugins {
    kotlin("jvm") version "1.8.21"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "io.runebox.mixin"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
    }
}

dependencies {
    implementation("org.ow2.asm:asm:_")
    implementation("org.ow2.asm:asm-commons:_")
    implementation("org.ow2.asm:asm-util:_")
    implementation("org.ow2.asm:asm-tree:_")
    implementation("com.google.guava:guava:_")
}