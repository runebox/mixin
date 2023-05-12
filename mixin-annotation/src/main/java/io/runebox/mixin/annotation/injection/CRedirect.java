package io.runebox.mixin.annotation.injection;

import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.annotation.mappings.AnnotationRemap;
import io.runebox.mixin.annotation.mappings.RemapType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redirect a specified method call or field access.<br>
 * <br>
 * <b>When redirecting a method call:</b><br>
 * The transformer method must have the same parameters and return type as the redirected method.<br>
 * If the redirected method is not static the instance of the method owner has to be the first parameter of the transformer method.<br>
 * <br>
 * <b>When redirecting a field access:</b><br>
 * The transformer method must have the same return type as the redirected field.<br>
 * If the redirected field is not static the instance of the field owner has to be the first parameter of the transformer method.<br>
 * No other parameters than the non-static instance are allowed.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CRedirect {

    /**
     * The method name and descriptor to inject into.<br>
     * This supports multiple targets and wildcards.<br>
     * e.g. print(Ljava/lang/String;)V
     *
     * @return The method name and descriptor
     */
    @AnnotationRemap(RemapType.SHORT_MEMBER)
    String[] method();

    /**
     * The target for the redirect.
     *
     * @return The target
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CTarget target();

    /**
     * The slice of instructions to search for the target.
     *
     * @return The slice
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CSlice slice() default @CSlice;

}
