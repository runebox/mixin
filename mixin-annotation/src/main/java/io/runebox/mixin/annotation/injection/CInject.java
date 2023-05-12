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
 * Inject into a method at various targets.<br>
 * The original code flow can be cancelled and own values can be returned.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CInject {

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
     * The targets for the injection.
     *
     * @return The targets
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CTarget[] target();

    /**
     * The slice of instructions to search for the target.
     *
     * @return The slice
     */
    @AnnotationRemap(RemapType.ANNOTATION)
    CSlice slice() default @CSlice;

    /**
     * Allow the original method to be cancelled.<br>
     * This is also required to change the return value.<br>
     * If this is set to false an exception will be thrown when trying to cancel the original method.
     *
     * @return If the original method can be cancelled
     */
    boolean cancellable() default false;

}
