plugins {
    `maven-publish`
}

dependencies {
    implementation("org.ow2.asm:asm:_")
    implementation("org.ow2.asm:asm-commons:_")
    implementation("org.ow2.asm:asm-util:_")
    implementation("org.ow2.asm:asm-tree:_")
    implementation("com.google.guava:guava:_")
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
    publications {
        create<MavenPublication>("mixin") {
            groupId = "io.runebox.mixin"
            artifactId = "mixin"
            version = project.version.toString()
            from(components["java"])
            artifacts {
                add("archives", tasks.jar.get())
            }
        }
    }
}