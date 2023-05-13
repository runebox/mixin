package io.runebox.mixin.gradle

import org.gradle.api.Project

open class MixinExtension(val project: Project) {
    var client: Project? = null
    var mixin: String? = null
    var output: String? = null
    var mainClass: String? = null
}