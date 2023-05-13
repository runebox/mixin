package io.runebox.mixin.annotation

import io.runebox.mixin.InvalidClass
import kotlin.reflect.KClass

annotation class Extends(
    val value: KClass<*> = InvalidClass::class,
    val name: String = ""
)
