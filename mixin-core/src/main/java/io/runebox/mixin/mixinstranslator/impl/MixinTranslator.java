package io.runebox.mixin.mixinstranslator.impl;

import io.runebox.mixin.annotation.CTransformer;
import io.runebox.mixin.utils.annotations.AnnotationParser;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Map;

class MixinTranslator implements IAnnotationTranslator {

    @Override
    public void translate(AnnotationNode annotation) {
        annotation.desc = Type.getDescriptor(CTransformer.class);
        Map<String, Object> values = AnnotationParser.listToMap(annotation.values);
        if (values.containsKey("targets")) values.put("name", values.remove("targets"));
        annotation.values = AnnotationParser.mapToList(values);
    }

}
