package io.runebox.mixin.transformer

import org.objectweb.asm.tree.MethodNode

class ReplaceTransformer : AbstractTransformer() {

    override fun visitReplacement(srcClass: String, dstClass: String, srcMethod: MethodNode, targetMethod: MethodNode) {
        targetMethod.instructions.clear()
        srcMethod.accept(MixinMethodVisitor(srcClass, dstClass, targetMethod))
    }
}