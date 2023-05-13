package io.runebox.mixin.transformer

import io.runebox.mixin.InjectionVisitor
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

abstract class AbstractTransformer : InjectionVisitor {

    override fun visitInjection(srcMethod: MethodNode, targetClass: ClassNode) {}

    override fun visitReplacement(srcClass: String, dstClass: String, srcMethod: MethodNode, targetMethod: MethodNode) {}

    override fun visitCopy(cls: ClassNode, desc: String, origName: String, newName: String) {}

    override fun visitPairing(src: ClassNode, target: ClassNode) {}

    override fun visitClassAnnotation(targetClass: ClassNode, annotation: AnnotationNode) {}

    override fun visitHook(methodToHook: MethodNode, methodToCall: MethodNode, owner: String, before: Boolean) {}

    override fun visitFieldInjection(targetClass: ClassNode, field: FieldNode, originalOwner: String) {}

    override fun visitFieldReplacement(method: MethodNode, fields: MutableList<String>) {}

    override fun visitMerge(src: MethodNode, dst: MethodNode) {}

    override fun visitInjectAtLine(src: MethodNode, dst: MethodNode, line: Int) {}

    override fun visitInstantiationReplacement(method: MethodNode, typesToReplace: HashMap<String, String>) {}

}