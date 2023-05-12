package io.runebox.mixin.transformer.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.AnnotationHandler;
import io.runebox.mixin.utils.mappings.MapRemapper;
import io.runebox.mixin.utils.mappings.Remapper;
import io.runebox.mixin.annotation.CShadow;
import io.runebox.mixin.exceptions.FieldNotFoundException;
import io.runebox.mixin.exceptions.MethodNotFoundException;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The annotation handler for the {@link CShadow} annotation.
 */
public class CShadowAnnotationHandler extends AnnotationHandler {

    @Override
    public void transform(MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer) {
        MapRemapper remapper = new MapRemapper();
        this.checkFields(mixinManager, transformedClass, transformer, remapper);
        this.checkMethods(mixinManager, transformedClass, transformer, remapper);

        if (remapper.isEmpty()) return;
        ClassNode mappedNode = Remapper.remap(transformer, remapper);
        Remapper.merge(transformer, mappedNode);
    }

    private void checkFields(final MixinManager mixinManager, ClassNode target, final ClassNode transformer, final MapRemapper remapper) {
        Iterator<FieldNode> it = transformer.fields.iterator();
        ClassNode origTarget = target;
        while (it.hasNext()) {
            target = origTarget;

            FieldNode field = it.next();
            CShadow annotation = this.getAnnotation(CShadow.class, field, mixinManager);
            if (annotation == null) continue;
            it.remove();

            if(!annotation.owner().isEmpty()) {
                target = mixinManager.getClassTree().getTreePart(mixinManager.getClassProvider(), annotation.owner()).getNode();
            }
            List<FieldNode> targets = ASMUtils.getFieldsFromCombi(target, annotation.value());
            if (targets.isEmpty()) throw new FieldNotFoundException(target, transformer, annotation.value());
            for (FieldNode targetField : targets) {
                if (field.name.equals(targetField.name) && field.desc.equals(targetField.desc)) continue;
                remapper.addFieldMapping(transformer.name, field.name, field.desc, targetField.name);
            }
        }
    }

    private void checkMethods(final MixinManager mixinManager, final ClassNode target, final ClassNode transformer, final MapRemapper remapper) {
        Iterator<MethodNode> it = transformer.methods.iterator();
        while (it.hasNext()) {
            MethodNode method = it.next();
            CShadow annotation = this.getAnnotation(CShadow.class, method, mixinManager);
            if (annotation == null) continue;
            it.remove();

            List<MethodNode> targets = ASMUtils.getMethodsFromCombi(target, annotation.value());
            if (targets.isEmpty()) throw new MethodNotFoundException(target, transformer, annotation.value());
            for (MethodNode targetMethod : targets) {
                if (method.name.equals(targetMethod.name) && method.desc.equals(targetMethod.desc)) continue;
                remapper.addMethodMapping(transformer.name, method.name, method.desc, targetMethod.name);
            }
        }
    }

}
