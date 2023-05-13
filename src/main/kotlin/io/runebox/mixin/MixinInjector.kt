package io.runebox.mixin

import com.google.common.reflect.ClassPath
import io.runebox.mixin.annotation.Mixin
import io.runebox.mixin.annotation.ReplaceInstantiation
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.lang.reflect.Modifier
import java.util.LinkedList
import java.util.jar.JarFile

class MixinInjector(val srcJar: File, val targetJar: File, val outputJar: File) {

    fun inject() {
        /*
         * Load and initialize everything we need to apply mixins from target -> src jars.
         */
        val srcClasses = readJar(srcJar)
        val targetClasses = readJar(targetJar)
        val pairings = pairClasses(srcClasses.values.toMutableList(), targetClasses.values.toMutableList())
        val typesToReplace = getTypesToReplace(srcClasses.values.toMutableList())
        val visitor = MixinInjectionVisitor(fetchVisitors())
        val results = LinkedList<ClassNode>()

        /*
         * Build the mixin pairings which represent a visitor transformation tree
         * of mixin transform jobs.
         *
         * This handles the ability to be able to have multiple mixins into the same stuff and smartly merge changes.
         */
        pairings.forEach { (src, target) ->
            val pairing = InjectionPairing(src, target, typesToReplace)
            pairing.accept(visitor)
            results.add(pairing.result())
        }
    }

    private fun pairClasses(srcClasses: MutableList<ClassNode>, targetClasses: MutableList<ClassNode>): HashMap<ClassNode, ClassNode> {
        val pairs = hashMapOf<ClassNode, ClassNode>()
        srcClasses.forEach { src ->
            val mixinCls = src.readAnnotation<Mixin>()
            if(mixinCls != null) {
                targetClasses.forEach { target ->
                    if(mixinCls == target.name) {
                        pairs[src] = target
                    }
                }
            }
        }
        return pairs
    }

    private inline fun <reified T> ClassNode.readAnnotation(): String? {
        val types = this.visibleAnnotations
            .filter { it.desc == Type.getDescriptor(T::class.java) }
            .map { it.values }
            .filter { it.size == 2 }
            .map { it[1] }
            .map { v -> when(v) {
                is Type -> v.internalName
                else -> v.toString()
            } }.toList()
        if(types.size != 1) return null
        return types[0].replace(".", "/")
    }

    private fun getTypesToReplace(nodes: MutableList<ClassNode>): HashMap<String, String> {
        val types = hashMapOf<String, String>()
        nodes.forEach { node ->
            val replaceTarget = node.readAnnotation<ReplaceInstantiation>()
            if(replaceTarget != null) {
                types[replaceTarget] = node.name
            }
        }
        return types
    }

    private fun fetchVisitors(): MutableList<InjectionVisitor> {
        val visitors = mutableListOf<InjectionVisitor>()
        ClassPath.from(MixinInjector::class.java.classLoader)
            .getTopLevelClassesRecursive("io.runebox.mixin.transformer")
            .map { it.name }
            .forEach { c ->
                try {
                    val klass = Class.forName(c)
                    if(klass.isAssignableFrom(InjectionVisitor::class.java) && !Modifier.isAbstract(klass.modifiers)) {
                        visitors.add(klass.newInstance() as InjectionVisitor)
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        return visitors
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val srcJar = File(args[0])
            val targetJar = File(args[1])
            val outputJar = File(args[2])
            MixinInjector(srcJar, targetJar, outputJar).inject()
        }

        private fun readJar(file: File): HashMap<String, ClassNode> {
            val ret = hashMapOf<String, ClassNode>()
            JarFile(file).use { jar ->
                jar.entries().asSequence().forEach { entry ->
                    if(entry.name.endsWith(".class")) {
                        val node = ClassNode()
                        val reader = ClassReader(jar.getInputStream(entry).readAllBytes())
                        reader.accept(node, ClassReader.SKIP_FRAMES)
                        ret[entry.name] = node
                    }
                }
            }
            return ret
        }
    }
}