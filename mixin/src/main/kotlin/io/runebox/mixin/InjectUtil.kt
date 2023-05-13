package io.runebox.mixin

import org.objectweb.asm.tree.MethodNode

object InjectUtil {

    fun Collection<MethodNode>.findMethod(name: String, desc: String): MethodNode? {
        this.forEach { method ->
            if(method.name == name && method.desc == desc) {
                return method
            }
        }
        return null
    }
}