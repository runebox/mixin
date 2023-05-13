package io.runebox.mixin.transformer

import org.objectweb.asm.tree.MethodNode

class ReplaceInstantiationTransformer : AbstractTransformer() {

    override fun visitInstantiationReplacement(method: MethodNode, typesToReplace: HashMap<String, String>) {
        val exceptions = method.exceptions.toTypedArray().copyOf()
        val copy = MethodNode(method.access, method.name, method.desc, method.signature, exceptions)
        val mv = InstantiationReplacementVisitor(typesToReplace, copy)
        method.accept(mv)
        method.instructions.clear()
        method.instructions.add(copy.instructions)
    }
}