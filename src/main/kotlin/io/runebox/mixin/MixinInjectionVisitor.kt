package io.runebox.mixin

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

open class MixinInjectionVisitor(private val visitors: MutableList<InjectionVisitor> = mutableListOf()) : InjectionVisitor {

    override fun visitInjection(srcMethod: MethodNode, targetClass: ClassNode) {
        visitors.forEach { it.visitInjection(srcMethod, targetClass) }
    }

    override fun visitReplacement(srcClass: String, dstClass: String, srcMethod: MethodNode, targetMethod: MethodNode) {
        visitors.forEach { it.visitReplacement(srcClass, dstClass, srcMethod, targetMethod) }
    }

    override fun visitCopy(cls: ClassNode, desc: String, origName: String, newName: String) {
        visitors.forEach { it.visitCopy(cls, desc, origName, newName) }
    }

    override fun visitPairing(src: ClassNode, target: ClassNode) {
        visitors.forEach { it.visitPairing(src, target) }
    }

    override fun visitClassAnnotation(targetClass: ClassNode, annotation: AnnotationNode) {
        visitors.forEach { it.visitClassAnnotation(targetClass, annotation) }
    }

    override fun visitHook(methodToHook: MethodNode, methodToCall: MethodNode, owner: String, before: Boolean) {
        visitors.forEach { it.visitHook(methodToHook, methodToCall, owner, before) }
    }

    override fun visitFieldInjection(targetClass: ClassNode, field: FieldNode, originalOwner: String) {
        visitors.forEach { it.visitFieldInjection(targetClass, field, originalOwner) }
    }

    override fun visitFieldReplacement(method: MethodNode, fields: MutableList<String>) {
        visitors.forEach { it.visitFieldReplacement(method, fields) }
    }

    override fun visitMerge(src: MethodNode, dst: MethodNode) {
        visitors.forEach { it.visitMerge(src, dst) }
    }

    override fun visitInjectAtLine(src: MethodNode, dst: MethodNode, line: Int) {
        visitors.forEach { it.visitInjectAtLine(src, dst, line) }
    }

    override fun visitInstantiationReplacement(method: MethodNode, typesToReplace: HashMap<String, String>) {
        visitors.forEach { it.visitInstantiationReplacement(method, typesToReplace) }
    }
}