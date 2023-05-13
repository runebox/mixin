package io.runebox.mixin.transformer

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

class FieldInjectTransformer : AbstractTransformer() {

    override fun visitFieldInjection(targetClass: ClassNode, field: FieldNode, originalOwner: String) {
        targetClass.fields.add(field)
    }
}