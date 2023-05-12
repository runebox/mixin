package io.runebox.mixin.annotation;

import io.runebox.mixin.annotation.mappings.AnnotationRemap;
import io.runebox.mixin.annotation.mappings.RemapType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify a slice of bytecode to make choosing the injection target easier.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CSlice {

    /**
     * The start of the slice.<br>
     * The target <b>must</b> match only a single instruction.
     *
     * @return The start of the slice
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CTarget from() default @CTarget("");

    /**
     * The end of the slice.<br>
     * The target <b>must</b> match only a single instruction.
     *
     * @return The end of the slice
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CTarget to() default @CTarget("");

}
