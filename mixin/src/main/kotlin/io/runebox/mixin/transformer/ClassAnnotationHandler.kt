package io.runebox.mixin.transformer

import org.objectweb.asm.tree.ClassNode

interface ClassAnnotationHandler {

    fun handle(targetClass: ClassNode, annotationValues: List<Any>)
}