package io.runebox.mixin.mixinstranslator.impl;

import io.runebox.mixin.annotation.CShadow;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

class ShadowTranslator implements IAnnotationTranslator {

    @Override
    public void translate(AnnotationNode annotation) {
        annotation.desc = Type.getDescriptor(CShadow.class);
    }

}
