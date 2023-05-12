plugins {
    `java-library`
    `maven-publish`
}

tasks.getByName<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map {
        if(it.isDirectory) it
        else zipTree(it)
    })
}


publishing {
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
    }

    publications {
        create<MavenPublication>("mixin-core") {
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