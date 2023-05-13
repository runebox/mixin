package io.runebox.mixin.transformer

import io.runebox.mixin.InjectionException
import io.runebox.mixin.annotation.Extends
import io.runebox.mixin.annotation.Handles
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

@Handles(Extends::class)
class HandleExtends : ClassAnnotationHandler {

    override fun handle(targetClass: ClassNode, annotationValues: List<Any>) {
        if(annotationValues.size != 2) {
            throw InjectionException("Failed to apply @Extends. Exactly one super type must be provided.")
        }
        targetClass.superName = getType(annotationValues)
    }

    private fun getType(values: List<Any>): String {
        val value = values[1]
        if(value is Type) {
            return value.internalName
        }
        return value.toString().replace(".", "/")
    }
}