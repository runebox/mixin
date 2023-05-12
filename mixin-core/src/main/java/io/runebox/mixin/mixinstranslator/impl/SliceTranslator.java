package io.runebox.mixin.mixinstranslator.impl;

import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.utils.annotations.AnnotationParser;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Map;

class SliceTranslator implements IAnnotationTranslator {

    @Override
    public void translate(AnnotationNode annotation) {
        annotation.desc = Type.getDescriptor(CSlice.class);
        Map<String, Object> values = AnnotationParser.listToMap(annotation.values);
        if (values.containsKey("from")) this.dynamicTranslate((AnnotationNode) values.get("from"));
        if (values.containsKey("to")) this.dynamicTranslate((AnnotationNode) values.get("to"));
        annotation.values = AnnotationParser.mapToList(values);
    }

}
