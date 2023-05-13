package io.runebox.mixin.annotation

import io.runebox.mixin.InvalidClass
import kotlin.reflect.KClass

annotation class Mixin(
    val value: KClass<*> = InvalidClass::class,
    val name: String = ""
)
