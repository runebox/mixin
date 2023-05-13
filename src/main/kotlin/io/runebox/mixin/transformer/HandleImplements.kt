package io.runebox.mixin.transformer

import io.runebox.mixin.annotation.Handles
import io.runebox.mixin.annotation.Implements
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.util.*

@Handles(Implements::class)
class HandleImplements : ClassAnnotationHandler {

    override fun handle(targetClass: ClassNode, annotationValues: List<Any>) {
        if(targetClass.interfaces == null) {
            targetClass.interfaces = listOf()
        }
        targetClass.interfaces.addAll(getInterfaceTypes(annotationValues))
    }

    private fun getInterfaceTypes(annotationValues: List<Any>): List<String> {
        val interfaces: MutableList<String> = LinkedList()
        var i = 1
        while (i < annotationValues.size) {
            val values = annotationValues[i] as List<*>
            for (value in values) {
                if (value is Type) {
                    interfaces.add(value.internalName)
                } else {
                    interfaces.add(value.toString().replace(".", "/"))
                }
            }
            i += 2
        }
        return interfaces
    }
}