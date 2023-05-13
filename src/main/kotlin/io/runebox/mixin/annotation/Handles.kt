package io.runebox.mixin.annotation

import kotlin.reflect.KClass

annotation class Handles(
    val value: KClass<*>
)
