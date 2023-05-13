package io.runebox.mixin.transformer

import com.google.common.reflect.ClassPath
import io.runebox.mixin.MixinInjector
import io.runebox.mixin.annotation.Handles
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import java.lang.reflect.Modifier

class ClassAnnotationVisitor(private val handlers: MutableList<ClassAnnotationHandler> = fetchHandlers()) : AbstractTransformer() {

    override fun visitClassAnnotation(targetClass: ClassNode, annotation: AnnotationNode) {
        handlers.forEach { handler ->
            val handlesAnnotation: Handles = handler.javaClass.getAnnotation(Handles::class.java)
            val type = Type.getType(handlesAnnotation.value.java)
            if(Type.getType(annotation.desc) == type) {
                handler.handle(targetClass, annotation.values)
        }
        }
    }

    companion object {
        private fun fetchHandlers(): MutableList<ClassAnnotationHandler> {
            val visitors = mutableListOf<ClassAnnotationHandler>()
            ClassPath.from(MixinInjector::class.java.classLoader)
                .getTopLevelClassesRecursive("io.runebox.mixin.transformer")
                .map { it.name }
                .forEach { c ->
                    try {
                        val klass = Class.forName(c)
                        val annotation = klass.getAnnotation(Handles::class.java)
                        if(annotation != null && ClassAnnotationHandler::class.java.isAssignableFrom(klass) &&
                            !Modifier.isAbstract(klass.modifiers)) {
                            visitors.add(klass.newInstance() as ClassAnnotationHandler)
                        }
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                }
            return visitors
        }
    }
}