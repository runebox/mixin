package io.runebox.mixin.mixinstranslator.impl;

import io.runebox.mixin.annotation.injection.COverride;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

class OverwriteTranslator implements IAnnotationTranslator {

    @Override
    public void translate(AnnotationNode annotation) {
        annotation.desc = Type.getDescriptor(COverride.class);
    }

}
