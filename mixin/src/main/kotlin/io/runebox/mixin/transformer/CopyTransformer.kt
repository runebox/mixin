package io.runebox.mixin.transformer

import io.runebox.mixin.InjectUtil.findMethod
import io.runebox.mixin.InjectionException
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class CopyTransformer : AbstractTransformer() {

    override fun visitCopy(cls: ClassNode, desc: String, origName: String, newName: String) {
        val method = cls.methods.findMethod(origName, desc)
            ?: throw InjectionException("Failed to transform @Copy. No such method: $origName$desc exists.")

        val exceptions = method.exceptions.toTypedArray().copyOf()
        val copy = MethodNode(method.access, newName, desc, method.signature, exceptions)

        method.accept(copy)
        cls.methods.add(copy)
    }
}