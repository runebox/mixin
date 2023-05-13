package io.runebox.mixin.annotation

import kotlin.reflect.KClass

annotation class Extends(
    val value: KClass<*> = InvalidClass::class,
    val name: String = ""
)
