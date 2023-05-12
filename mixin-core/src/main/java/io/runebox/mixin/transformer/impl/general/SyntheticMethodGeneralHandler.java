package io.runebox.mixin.transformer.impl.general;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.AnnotationHandler;
import io.runebox.mixin.utils.mappings.MapRemapper;
import io.runebox.mixin.utils.mappings.Remapper;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

/**
 * Rename synthetic methods in transformers to avoid conflicts with the injected class.
 */
public class SyntheticMethodGeneralHandler extends AnnotationHandler {

    static void fillSyntheticMappings(final ClassNode source, final ClassNode target, final MapRemapper remapper) {
        for (MethodNode method : source.methods) {
            if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) continue;
            String newName = getUniqueName(target, source, method);
            remapper.addMethodMapping(source.name, method.name, method.desc, newName);
        }
    }

    private static String getUniqueName(final ClassNode target, final ClassNode owner, final MethodNode method) {
        int id = 0;
        String current;
        do {
            current = method.name + "$" + getSimpleName(owner) + id;
        } while (ASMUtils.getMethod(target, current, method.desc) != null);
        return current;
    }

    private static String getSimpleName(final ClassNode node) {
        int index = node.name.lastIndexOf('/');
        return index == -1 ? node.name : node.name.substring(index + 1);
    }


    @Override
    public void transform(MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer) {
        MapRemapper remapper = new MapRemapper();
        fillSyntheticMappings(transformer, transformedClass, remapper);
        ClassNode remapped = Remapper.remap(transformer, remapper);
        Remapper.merge(transformer, remapped);
    }

}
