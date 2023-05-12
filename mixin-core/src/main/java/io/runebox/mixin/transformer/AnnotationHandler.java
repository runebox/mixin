package io.runebox.mixin.transformer;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.utils.mappings.Remapper;
import io.runebox.mixin.annotation.InjectionInfo;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.annotations.AnnotationParser;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.runebox.mixin.utils.Types.typeDescriptor;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

/**
 * The abstract base for all annotation handlers.
 */
public abstract class AnnotationHandler {

    /**
     * Handle all annotations in the transformer class.
     *
     * @param mixinManager The transformer manager
     * @param injectionTargets   The available injection targets
     * @param transformedClass   The target class node
     * @param transformer        The transformer class node
     */
    public abstract void transform(final MixinManager mixinManager, final Map<String, IInjectionTarget> injectionTargets, final ClassNode transformedClass, final ClassNode transformer);

    /**
     * Get a parsed annotation from a class node.
     *
     * @param <T>                The annotation type
     * @param annotationClass    The annotation class
     * @param classNode          The class node
     * @param mixinManager The transformer manager
     * @return The parsed annotation or null if not found
     */
    protected <T extends Annotation> T getAnnotation(final Class<T> annotationClass, final ClassNode classNode, final MixinManager mixinManager) {
        T annotation = this.getAnnotation(annotationClass, classNode.visibleAnnotations, mixinManager);
        if (annotation == null) annotation = this.getAnnotation(annotationClass, classNode.invisibleAnnotations, mixinManager);
        return annotation;
    }

    /**
     * Get a parsed annotation from a field node.
     *
     * @param <T>                The annotation type
     * @param annotationClass    The annotation class
     * @param field              The field node
     * @param mixinManager The transformer manager
     * @return The parsed annotation or null if not found
     */
    protected <T extends Annotation> T getAnnotation(final Class<T> annotationClass, final FieldNode field, final MixinManager mixinManager) {
        T annotation = this.getAnnotation(annotationClass, field.visibleAnnotations, mixinManager);
        if (annotation == null) annotation = this.getAnnotation(annotationClass, field.invisibleAnnotations, mixinManager);
        return annotation;
    }

    /**
     * Get a parsed annotation from a method node.
     *
     * @param <T>                The annotation type
     * @param annotationClass    The annotation class
     * @param method             The method node
     * @param mixinManager The transformer manager
     * @return The parsed annotation or null if not found
     */
    protected <T extends Annotation> T getAnnotation(final Class<T> annotationClass, final MethodNode method, final MixinManager mixinManager) {
        T annotation = this.getAnnotation(annotationClass, method.visibleAnnotations, mixinManager);
        if (annotation == null) annotation = this.getAnnotation(annotationClass, method.invisibleAnnotations, mixinManager);
        return annotation;
    }

    /**
     * Get a parsed annotation from a list of annotation nodes.
     *
     * @param <T>                The annotation type
     * @param annotationClass    The annotation class
     * @param annotations        The annotation nodes
     * @param mixinManager The transformer manager
     * @return The parsed annotation or null if not found
     */
    protected <T extends Annotation> T getAnnotation(final Class<T> annotationClass, final List<AnnotationNode> annotations, final MixinManager mixinManager) {
        if (annotations != null) {
            for (AnnotationNode annotation : annotations) {
                if (annotation.desc.equals(typeDescriptor(annotationClass))) {
                    return AnnotationParser.parse(annotationClass, mixinManager, AnnotationParser.listToMap(annotation.values));
                }
            }
        }
        return null;
    }

    /**
     * Add the {@link InjectionInfo} annotation to the given method.
     *
     * @param transformer The transformer class node
     * @param method      The method node
     */
    protected void prepareForCopy(final ClassNode transformer, final MethodNode method) {
        AnnotationNode injectionInfo = new AnnotationNode(typeDescriptor(InjectionInfo.class));
        injectionInfo.values = Arrays.asList(
                "transformer", transformer.name,
                "originalName", method.name + method.desc
        );
        if (method.invisibleAnnotations == null) method.invisibleAnnotations = new ArrayList<>();
        method.invisibleAnnotations.add(injectionInfo);
    }

    /**
     * Rename a method and add it to the target class node.
     *
     * @param injectionMethod  The transformer method node
     * @param targetMethod     The target method node
     * @param transformer      The transformer class node
     * @param transformedClass The target class node
     * @param extra            Extra data for the generated method name
     */
    protected void renameAndCopy(final MethodNode injectionMethod, final MethodNode targetMethod, final ClassNode transformer, final ClassNode transformedClass, final String extra) {
        this.prepareForCopy(transformer, injectionMethod);
        int i = 0;
        String baseName = injectionMethod.name + "$" + targetMethod.name.replaceAll("[<>]", "") + "$" + extra;
        do {
            injectionMethod.name = baseName + i++;
        } while (this.hasMethod(transformedClass, injectionMethod.name));
        Remapper.remapAndAdd(transformer, transformedClass, injectionMethod);
    }


    private boolean hasMethod(final ClassNode node, final String name) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name)) return true;
        }
        return false;
    }

}
