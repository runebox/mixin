package io.runebox.mixin

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

interface InjectionVisitor {

    fun visitInjection(srcMethod: MethodNode, targetClass: ClassNode)

    fun visitReplacement(srcClass: String, dstClass: String, srcMethod: MethodNode, targetMethod: MethodNode)

    fun visitCopy(cls: ClassNode, desc: String, origName: String, newName: String)

    fun visitPairing(src: ClassNode, target: ClassNode)

    fun visitClassAnnotation(targetClass: ClassNode, annotation: AnnotationNode)

    fun visitHook(methodToHook: MethodNode, methodToCall: MethodNode, owner: String, before: Boolean)

    fun visitFieldInjection(targetClass: ClassNode, field: FieldNode, originalOwner: String)

    fun visitFieldReplacement(method: MethodNode, fields: MutableList<String>)

    fun visitMerge(src: MethodNode, dst: MethodNode)

    fun visitInjectAtLine(src: MethodNode, dst: MethodNode, line: Int)

    fun visitInstantiationReplacement(method: MethodNode, typesToReplace: HashMap<String, String>)

}