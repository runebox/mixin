package io.runebox.mixin

import io.runebox.mixin.InjectUtil.findMethod
import io.runebox.mixin.annotation.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

class InjectionPairing(srcClass: ClassNode, targetClass: ClassNode, val typesToReplace: HashMap<String, String>) {

    val srcClass = ClassNode()
    val targetClass = ClassNode()

    init {
        srcClass.accept(this.srcClass)
        targetClass.accept(this.targetClass)
    }

    fun result() = targetClass

    fun accept(visitor: InjectionVisitor) {
        srcClass.methods.forEach { method ->
            // @Copy Annotation
            val copyMethodName = method.getCopyMethodName()
            if(copyMethodName != null) {
                visitor.visitCopy(targetClass, method.desc, copyMethodName, method.name)
            }
        }

        // @Replace Annotation
        srcClass.methods.forEach { method ->
            val replaceMethodName = method.getReplaceMethodName()
            if(replaceMethodName != null) {
                val target = targetClass.methods.findMethod(replaceMethodName, method.desc)
                if(target != null) {
                    visitor.visitReplacement(srcClass.name, targetClass.name, method, target)
                } else {
                    throw InjectionException("Failed to apply @Replace. Could not find the method to replace.")
                }
            }
        }

        // @HookAfter Annotation
        srcClass.methods.forEach { method ->
            val hookAfterMethodName = method.getHookAfterMethodName()
            val hookBeforeMethodName = method.getHookBeforeMethodName()
            if(hookAfterMethodName != null) {
                val methodToHook = targetClass.methods.findMethod(hookAfterMethodName, method.desc)
                    ?: throw InjectionException("Failed to apply @HookAfter. Hook method: $hookAfterMethodName not found.")
                visitor.visitHook(methodToHook, method, targetClass.name, false)
            }

            // @HookBefore Annotation
            if(hookBeforeMethodName != null) {
                val methodToHook = targetClass.methods.findMethod(hookBeforeMethodName, method.desc)
                    ?: throw InjectionException("Failed to apply @HookBefore. Hook method: $hookBeforeMethodName not found.")
                visitor.visitHook(methodToHook, method, targetClass.name, true)
            }

            // @Inject Annotation
            if(hookAfterMethodName != null || hookBeforeMethodName != null) {
                visitor.visitInjection(method, targetClass)
            }
        }

        // @Inject (Methods) Annotation
        srcClass.methods.forEach { method ->
            if(method.shouldInject()) {
                visitor.visitInjection(method, targetClass)
            }
        }

        // @Inject (Fields) Annotation
        srcClass.fields.forEach { field ->
            if(field.shouldInject()) {
                visitor.visitFieldInjection(targetClass, field, srcClass.name)
            }
        }

        visitor.visitPairing(srcClass, targetClass)

        val srcClinit = srcClass.methods.findMethod("<clinit>", "()V")
        val dstClinit = targetClass.methods.findMethod("<clinit>", "()V")

        if(srcClinit != null) {
            if(dstClinit === null) {
                visitor.visitInjection(srcClinit, targetClass)
            } else {
                visitor.visitMerge(srcClinit, dstClinit)
            }
        }

        // @InlineHookAt
        srcClass.methods.forEach { method ->
            val annotation = method.getAnnotationType<InlineHookAt>()
            if(annotation != null) {
                val values = annotation.values
                val target = annotation.getOrDefault<String?>("value", null)
                val desc = annotation.getOrDefault("desc", "()V")
                val line = annotation.getOrDefault("line", 0)
                val targetMethod = targetClass.methods.findMethod(target ?: "", desc)

                if(target == null) {
                    throw InjectionException("Failed to apply @InlineHookAt. The target method name must be provided.")
                }

                if(Type.getReturnType(method.desc) != Type.VOID_TYPE) {
                    throw InjectionException("Failed to apply @InlineHookAt. Inline injected methods must return void.")
                }

                if(targetMethod != null) {
                    visitor.visitInjectAtLine(method, targetMethod, line)
                } else {
                    throw InjectionException("Failed to apply @InjectAtLine. Target method: $target$desc was not found.")
                }
            }
        }

        targetClass.methods.forEach { method ->
            visitor.visitInstantiationReplacement(method, typesToReplace)
        }

        val fieldsToReplace = srcClass.fields.getShouldReplace()
        targetClass.methods.forEach { method ->
            visitor.visitFieldReplacement(method, fieldsToReplace.toMutableList())
        }

        if(srcClass.visibleAnnotations != null) {
            srcClass.visibleAnnotations.forEach { annotation ->
                if(Type.getType(ReplaceInstantiation::class.java) != Type.getType(annotation.desc)) {
                    visitor.visitClassAnnotation(targetClass, annotation)
                }
            }
        }
    }

    private fun MethodNode.getCopyMethodName() = this.readAnnotation<Copy>()

    private fun MethodNode.getReplaceMethodName() = this.readAnnotation<Replace>()

    private fun MethodNode.getHookBeforeMethodName() = this.readAnnotation<HookBefore>()

    private fun MethodNode.getHookAfterMethodName() = this.readAnnotation<HookAfter>()

    private fun FieldNode.shouldInject(): Boolean {
        if(visibleAnnotations == null) return false
        visibleAnnotations.forEach { annotation ->
            if(Type.getType(Inject::class.java) == Type.getType(annotation.desc)) {
                return true
            }
        }
        return false
    }

    private fun MethodNode.shouldInject(): Boolean {
        if(visibleAnnotations == null) return false
        visibleAnnotations.forEach { annotation ->
            if(Type.getType(Inject::class.java) == Type.getType(annotation.desc)) {
                return true
            }
        }
        return false
    }

    private fun Collection<FieldNode>.getShouldReplace(): List<String> {
        val toReplace = mutableListOf<String>()
        this.forEach { field ->
           field.visibleAnnotations.forEach { annotation ->
               if(Type.getType(Inject::class.java) == Type.getType(annotation.desc) ||
                   Type.getType(Shadow::class.java) == Type.getType(annotation.desc)) {
                   toReplace.add(field.name)
               }
           }
        }
        return toReplace
    }

    private inline fun <reified T> MethodNode.getAnnotationType(): AnnotationNode? {
        if(visibleAnnotations == null) return null
        visibleAnnotations.forEach { annotation ->
            if(Type.getType(T::class.java) == Type.getType(annotation.desc)) {
                return annotation
            }
        }
        return null
    }

    private inline fun <reified T> MethodNode.readAnnotation(): String? {
        if(visibleAnnotations == null) { visibleAnnotations = mutableListOf() }
        return visibleAnnotations.filter { it.desc == Type.getDescriptor(T::class.java) }
            .map { it.values }
            .filter { it.size == 2 }
            .map { it[1] }
            .map { when(it) {
                is Type -> it.descriptor
                else -> it.toString()
            } }.let {
                if(it.size != 1) return@let null
                else return@let it[0].replace(".", "/")
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> AnnotationNode.getOrDefault(key: Any?, defaultValue: T): T {
        val values = this.values
        for(i in 0 until values.size) {
            val j = i * 2
            if(values[0] == key) {
                return values[i + 1] as T
            }
        }
        return defaultValue
    }
}