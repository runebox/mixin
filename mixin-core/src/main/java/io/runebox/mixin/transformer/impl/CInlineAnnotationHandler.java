package io.runebox.mixin.transformer.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.AnnotationHandler;
import io.runebox.mixin.annotation.CInline;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.MethodInliner;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.runebox.mixin.utils.Types.typeDescriptor;

/**
 * The annotation handler for the {@link CInline} annotation.
 */
public class CInlineAnnotationHandler extends AnnotationHandler {

    @Override
    public void transform(MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer) {
        List<MethodNode> methodsToInline = transformedClass.methods
                .stream()
                .filter(methodNode -> methodNode.invisibleAnnotations != null)
                .filter(methodNode -> methodNode.invisibleAnnotations
                        .stream()
                        .anyMatch(annotation -> annotation.desc.equals(typeDescriptor(CInline.class))))
                .collect(Collectors.toList());
        for (MethodNode methodNode : methodsToInline) MethodInliner.wrappedInline(transformedClass, methodNode, transformedClass.name);
    }

}
