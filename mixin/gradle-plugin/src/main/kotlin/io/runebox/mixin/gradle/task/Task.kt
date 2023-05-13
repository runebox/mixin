package io.runebox.mixin.gradle.task

import io.runebox.mixin.gradle.MixinExtension
import org.gradle.api.DefaultTask

open class Task : DefaultTask() {

    private val extension = project.extensions.getByType(MixinExtension::class.java)

}