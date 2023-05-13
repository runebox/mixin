plugins {
    `maven-publish`
    `java-gradle-plugin`
}

group = "io.runebox.mixin.gradle"
version = project.version.toString()

repositories {
    mavenLocal()
    maven(url = "https://jitpack.io")
    mavenCentral()
}

dependencies {

}

artifacts {
    add("archives", tasks.jar.get())
}

gradlePlugin {
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
    plugins {
        create("mixin-gradle") {
            id = "io.runebox.mixin.gradle"
            version = project.version.toString()
            implementationClass = "io.runebox.mixin.gradle.MixinGradlePlugin"
            displayName = "RuneBox Mixin Gradle Plugin"
        }
    }
    isAutomatedPublishing = true
}

publishing {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io/")
    }
    publications {
        create<MavenPublication>("mixin-gradle") {
            groupId = "io.runebox.mixin.gradle"
            artifactId = "mixin-gradle"
            version = project.version.toString()
            from(components["java"])
        }
    }
}