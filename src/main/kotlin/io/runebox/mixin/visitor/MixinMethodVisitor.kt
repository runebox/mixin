package io.runebox.mixin.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9

class MixinMethodVisitor(
    private val srcClass: String,
    private val targetClass: String,
    parent: MethodVisitor
) : MethodVisitor(ASM9, parent) {

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        if(owner == srcClass) {
            super.visitFieldInsn(opcode, targetClass, name, descriptor)
        } else {
            super.visitFieldInsn(opcode, owner, name, descriptor)
        }
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String?,
        descriptor: String,
        isInterface: Boolean
    ) {
        if(owner == srcClass) {
            super.visitMethodInsn(opcode, targetClass, name, descriptor, isInterface)
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
    }
}