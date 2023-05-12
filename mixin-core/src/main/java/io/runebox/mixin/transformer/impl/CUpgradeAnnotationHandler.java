package io.runebox.mixin.transformer.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.AnnotationHandler;
import io.runebox.mixin.annotation.CUpgrade;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.annotations.IParsedAnnotation;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

/**
 * The annotation handler for the {@link CUpgrade} annotation.
 */
public class CUpgradeAnnotationHandler extends AnnotationHandler {

    @Override
    public void transform(MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer) {
        CUpgrade annotation = this.getAnnotation(CUpgrade.class, transformer, mixinManager);
        if (annotation == null) return;

        int version;
        if (((IParsedAnnotation) annotation).wasSet("value")) version = annotation.value();
        else version = transformer.version;
        transformedClass.version = Math.max(transformedClass.version, version);
    }

}
