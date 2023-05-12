plugins {
    `java-library`
    `maven-publish`
}

publishing {
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
    }

    publications {
        create<MavenPublication>("mixin-annotation") {
            groupId = "io.runebox.mixin"
            artifactId = "mixin-annotation"
            version = project.version.toString()
            from(components["java"])
            artifacts {
                add("archives", tasks.jar.get())
            }
        }
    }
}