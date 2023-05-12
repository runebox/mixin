package io.runebox.mixin.transformer.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.types.RemovingTargetAnnotationHandler;
import io.runebox.mixin.utils.mappings.Remapper;
import io.runebox.mixin.annotation.injection.COverride;
import io.runebox.mixin.exceptions.TransformerException;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import io.runebox.mixin.utils.Codifier;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.Map;

import static io.runebox.mixin.utils.Types.argumentTypes;

/**
 * The annotation handler for the {@link COverride} annotation.
 */
public class COverrideAnnotationHandler extends RemovingTargetAnnotationHandler<COverride> {

    public COverrideAnnotationHandler() {
        super(COverride.class, COverride::value);
    }

    @Override
    public void transform(COverride annotation, MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer, MethodNode transformerMethod, MethodNode target) {
        if (Modifier.isStatic(target.access) != Modifier.isStatic(transformerMethod.access)) {
            boolean isStatic = Modifier.isStatic(target.access);
            throw new TransformerException(transformerMethod, transformer, "must " + (isStatic ? "" : "not ") + "be static")
                    .help(Codifier.of(transformerMethod).access(isStatic ? transformerMethod.access | Modifier.STATIC : transformerMethod.access & ~Modifier.STATIC));
        }
        if (!ASMUtils.compareTypes(argumentTypes(target.desc), argumentTypes(transformerMethod.desc))) {
            throw new TransformerException(transformerMethod, transformer, "must have the same arguments as the target method")
                    .help(Codifier.of(target));
        }
        if (ASMUtils.isAccessLower(transformerMethod.access, target.access)) {
            throw new TransformerException(transformerMethod, transformer, "must be higher/equal to original method");
        }
        transformerMethod.name = target.name;
        transformerMethod.desc = target.desc;
        transformedClass.methods.remove(target);
        this.prepareForCopy(transformer, transformerMethod);
        Remapper.remapAndAdd(transformer, transformedClass, transformerMethod);
    }

}
