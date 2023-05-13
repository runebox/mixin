package io.runebox.mixin.transformer

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class InstantiationReplacementVisitor(private val typesToReplace: Map<String, String>, parent: MethodVisitor) : MethodVisitor(ASM9, parent) {

    override fun visitTypeInsn(opcode: Int, type: String?) {
        if(typesToReplace.containsKey(type) && opcode == NEW) {
            super.visitTypeInsn(opcode, typesToReplace[type])
        } else {
            super.visitTypeInsn(opcode, type)
        }
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String?,
        descriptor: String,
        isInterface: Boolean
    ) {
        if(typesToReplace.containsKey(owner) && opcode == INVOKESPECIAL) {
            super.visitMethodInsn(opcode, typesToReplace[owner], name, descriptor, isInterface)
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }
}