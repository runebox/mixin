plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":mixin-annotation"))
    api("org.ow2.asm:asm:_")
    api("org.ow2.asm:asm-commons:_")
    api("org.ow2.asm:asm-util:_")
    api("org.ow2.asm:asm-tree:_")
    implementation("com.google.guava:guava:_")
    implementation("org.tinylog:tinylog-api-kotlin:_")
    implementation("org.tinylog:tinylog-impl:_")
}


publishing {
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
    }

    publications {
        create<MavenPublication>("mixin-core") {
            groupId = "io.runebox.mixin"
            artifactId = "mixin-core"
            version = project.version.toString()
            from(components["java"])
            artifacts {
                add("archives", tasks.jar.get())
            }
        }
    }
}