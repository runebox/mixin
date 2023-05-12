package io.runebox.mixin.annotation.injection;


import io.runebox.mixin.annotation.mappings.AnnotationRemap;
import io.runebox.mixin.annotation.mappings.FillType;
import io.runebox.mixin.annotation.mappings.RemapType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject into a method using direct ASM.<br>
 * You get direct access to the class/method node of the target.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CASM {

    /**
     * The method name and descriptor to inject into.<br>
     * This supports multiple targets and wildcards.<br>
     * To get the class node keep the target empty.<br>
     * e.g. print(Ljava/lang/String;)V
     *
     * @return The method name and descriptor
     */
    @AnnotationRemap(value = RemapType.SHORT_MEMBER, fill = FillType.KEEP_EMPTY)
    String[] value() default {};

    /**
     * The shift of the CASM injection (before or after the other transformer).
     *
     * @return The shift
     */
    Shift shift() default Shift.TOP;


    enum Shift {
        /**
         * Execute the transformer at the top of the handler chain.
         */
        TOP,
        /**
         * Execute the transformer at the bottom of the handler chain.
         */
        BOTTOM
    }

}
