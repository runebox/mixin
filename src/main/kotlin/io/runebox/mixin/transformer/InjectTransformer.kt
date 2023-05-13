package io.runebox.mixin.transformer

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class InjectTransformer : AbstractTransformer() {

    override fun visitInjection(srcMethod: MethodNode, targetClass: ClassNode) {
        val exceptions = srcMethod.exceptions.toTypedArray().copyOf()
        val copy = MethodNode(srcMethod.access, srcMethod.name, srcMethod.desc, srcMethod.signature, exceptions)
        srcMethod.accept(copy)
        targetClass.methods.add(copy)
    }
}