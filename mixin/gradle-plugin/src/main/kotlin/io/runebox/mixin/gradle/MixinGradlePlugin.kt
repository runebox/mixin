package io.runebox.mixin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class MixinGradlePlugin : Plugin<Project> {

    override fun apply(proj: Project) {
        proj.logger.lifecycle("========================================")
        proj.logger.lifecycle("= RuneBox Mixin Injector Gradle Plugin =")
        proj.logger.lifecycle("=           By Kyle Escobar            =")
        proj.logger.lifecycle("= https://github.com/runebox/mixin.git =")
        proj.logger.lifecycle("========================================")

        proj.extensions.create("mixin", MixinExtension::class.java,  proj)
        proj.afterEvaluate { project ->
            project.repositories.maven {
                it.setUrl("https://jitpack.io/")
            }

            project.repositories.mavenCentral()
            project.repositories.mavenLocal()
            val extension = project.extensions.getByType(MixinExtension::class.java)

            project.plugins.apply("java")
            project.plugins.apply("idea")

            project.dependencies.add("implementation", project.dependencies.create(project.project(":mixin")))
            project.dependencies.add("implementation", project.dependencies.create(project.files(extension.client)))
        }
    }
}