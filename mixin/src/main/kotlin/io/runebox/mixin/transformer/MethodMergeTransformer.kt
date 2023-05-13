package io.runebox.mixin.transformer

import org.objectweb.asm.tree.MethodNode

class MethodMergeTransformer : AbstractTransformer() {

    override fun visitMerge(src: MethodNode, dst: MethodNode) {
        val copy = MethodNode(dst.access, dst.name, dst.desc, dst.signature, emptyArray())
        val mv = ReturnValueRemoveVisitor(copy)
        src.accept(mv)
        dst.accept(copy)
        dst.instructions.clear()
        dst.instructions.add(copy.instructions)
    }
}