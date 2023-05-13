package io.runebox.mixin.annotation

annotation class InlineHookAt(
    val value: String,
    val desc: String = "()V",
    val line: Int = 0
)
