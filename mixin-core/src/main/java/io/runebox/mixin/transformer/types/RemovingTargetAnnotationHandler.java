package io.runebox.mixin.transformer.types;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.exceptions.MethodNotFoundException;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An abstract annotation handler which handles all annotations of the given type.<br>
 * The handled transformer methods are removed from the transformer class afterwards.<br>
 * The target methods of the transformer are automatically parsed.
 *
 * @param <T> The annotation type
 */
public abstract class RemovingTargetAnnotationHandler<T extends Annotation> extends RemovingAnnotationHandler<T> {

    private final Function<T, String[]> targetCombis;

    public RemovingTargetAnnotationHandler(final Class<T> annotationClass, final Function<T, String[]> targetCombis) {
        super(annotationClass);
        this.targetCombis = targetCombis;
    }

    @Override
    public final void transform(T annotation, MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer, MethodNode transformerMethod) {
        for (String targetCombi : this.targetCombis.apply(annotation)) {
            List<MethodNode> targets = ASMUtils.getMethodsFromCombi(transformedClass, targetCombi);
            if (targets.isEmpty()) throw new MethodNotFoundException(transformedClass, transformer, targetCombi);
            for (MethodNode target : targets) {
                this.transform(annotation, mixinManager, injectionTargets, transformedClass, transformer, ASMUtils.cloneMethod(transformerMethod), target);
            }
        }
    }

    /**
     * Handle a transformer method of the transformer with the given annotation.
     *
     * @param annotation         The annotation of the transformer method
     * @param mixinManager The transformer manager
     * @param injectionTargets   The available injection targets
     * @param transformedClass   The target class node
     * @param transformer        The transformer class node
     * @param transformerMethod  The method node of the transformer
     * @param target             The target method node
     */
    public abstract void transform(final T annotation, final MixinManager mixinManager, final Map<String, IInjectionTarget> injectionTargets, final ClassNode transformedClass, final ClassNode transformer, final MethodNode transformerMethod, final MethodNode target);

}
