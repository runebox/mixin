package io.runebox.mixin.transformer.impl.general;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.AnnotationHandler;
import io.runebox.mixin.utils.mappings.MapRemapper;
import io.runebox.mixin.utils.mappings.Remapper;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

import static io.runebox.mixin.utils.ASMUtils.dot;

/**
 * Remap and make transformer inner classes accessible from the transformed classes.
 */
public class InnerClassGeneralHandler extends AnnotationHandler {

    @Override
    public void transform(MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode injectedClass, ClassNode transformer) {
        final ClassNode fInjectedClass = ASMUtils.cloneClass(injectedClass);
        final ClassNode fTransformer = ASMUtils.cloneClass(transformer);
        boolean hasInnerClasses = false;
        for (InnerClassNode innerClass : transformer.innerClasses) {
            if (innerClass.outerName != null) continue;
            hasInnerClasses = true;

            mixinManager.addRawTransformer(dot(innerClass.name), (tm, transformedClass) -> {
                for (MethodNode method : transformedClass.methods) method.access = ASMUtils.setAccess(method.access, Opcodes.ACC_PUBLIC);
                for (FieldNode field : transformedClass.fields) field.access = ASMUtils.setAccess(field.access, Opcodes.ACC_PUBLIC);
                transformedClass.access = ASMUtils.setAccess(transformedClass.access, Opcodes.ACC_PUBLIC);
                transformedClass.outerClass = null;

                MapRemapper remapper = new MapRemapper();
                remapper.addClassMapping(fTransformer.name, fInjectedClass.name);
                SyntheticMethodGeneralHandler.fillSyntheticMappings(fTransformer, fInjectedClass, remapper);
                return Remapper.remap(transformedClass, remapper);
            });
        }
        if (hasInnerClasses) {
            for (MethodNode method : transformer.methods) {
                if ((method.access & Opcodes.ACC_SYNTHETIC) != 0) {
                    method.access = ASMUtils.setAccess(method.access, Opcodes.ACC_PUBLIC);
                    method.access &= ~Opcodes.ACC_BRIDGE;
                }
            }
        }
    }

}
