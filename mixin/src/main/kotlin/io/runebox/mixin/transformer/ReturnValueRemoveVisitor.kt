package io.runebox.mixin.transformer

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class ReturnValueRemoveVisitor(parent: MethodVisitor) : MethodVisitor(ASM9, parent) {

    override fun visitInsn(opcode: Int) {
        var pop = POP
        when(opcode) {
            DRETURN, LRETURN -> {
                pop = POP2
            }
            ARETURN, FRETURN, IRETURN -> {
                super.visitInsn(pop)
            }
            RETURN -> return
        }
        super.visitInsn(opcode)
    }
}