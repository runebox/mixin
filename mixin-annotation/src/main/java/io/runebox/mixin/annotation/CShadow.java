package io.runebox.mixin.annotation;


import io.runebox.mixin.annotation.mappings.AnnotationRemap;
import io.runebox.mixin.annotation.mappings.RemapType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create a shadow copy of a method/field/constructor to directly access the original member after injection.<br>
 * When shadowing a method you can simply make the method native to skip the need of a method body.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface CShadow {

    /**
     * The name of the member to shadow.<br>
     * When left empty the name and descriptor of the annotated member will be used.<br>
     * e.g. print(Ljava/lang/String;)V
     *
     * @return The name of the member to shadow
     */
    @AnnotationRemap(RemapType.SHORT_MEMBER)
    String value() default "";

    @AnnotationRemap(RemapType.CLASS)
    String owner() default "";

}
