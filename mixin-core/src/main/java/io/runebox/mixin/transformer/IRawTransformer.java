package io.runebox.mixin.transformer;

import io.runebox.mixin.MixinManager;
import org.objectweb.asm.tree.ClassNode;

/**
 * A transformer which has access to the parsed class node of the transformed class.
 */
public interface IRawTransformer {

    /**
     * Transform the target class node.
     *
     * @param mixinManager The transformer manager
     * @param transformedClass   The target class node
     * @return The transformed class node or the same if nothing was changed
     */
    ClassNode transform(final MixinManager mixinManager, final ClassNode transformedClass);

}
