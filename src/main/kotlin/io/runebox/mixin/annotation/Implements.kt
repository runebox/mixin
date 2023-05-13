package io.runebox.mixin.annotation

import kotlin.reflect.KClass

annotation class Implements(
    val value: Array<KClass<*>> = [],
    val name: Array<String> = []
)
