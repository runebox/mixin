package io.runebox.mixin.transformer.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.transformer.types.RemovingAnnotationHandler;
import io.runebox.mixin.annotation.injection.CRedirect;
import io.runebox.mixin.exceptions.InvalidTargetException;
import io.runebox.mixin.exceptions.MethodNotFoundException;
import io.runebox.mixin.exceptions.TransformerException;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.transformer.impl.credirect.CRedirectField;
import io.runebox.mixin.transformer.impl.credirect.CRedirectInvoke;
import io.runebox.mixin.transformer.impl.credirect.CRedirectNew;
import io.runebox.mixin.transformer.impl.credirect.IRedirectTarget;
import io.runebox.mixin.utils.ASMUtils;
import io.runebox.mixin.utils.Codifier;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The annotation handler for the {@link CRedirect} annotation.
 */
public class CRedirectAnnotationHandler extends RemovingAnnotationHandler<CRedirect> {

    private final Map<String, IRedirectTarget> redirectTargets = new HashMap<>();

    public CRedirectAnnotationHandler() {
        super(CRedirect.class);

        this.redirectTargets.put("INVOKE", new CRedirectInvoke());
        this.redirectTargets.put("FIELD", new CRedirectField());
        this.redirectTargets.put("GETFIELD", new CRedirectField());
        this.redirectTargets.put("PUTFIELD", new CRedirectField());
        this.redirectTargets.put("NEW", new CRedirectNew());
    }

    @Override
    public void transform(CRedirect annotation, MixinManager mixinManager, Map<String, IInjectionTarget> injectionTargets, ClassNode transformedClass, ClassNode transformer, MethodNode transformerMethod) {
        IInjectionTarget iInjectionTarget = injectionTargets.get(annotation.target().value().toUpperCase(Locale.ROOT));
        IRedirectTarget iRedirectTarget = this.redirectTargets.get(annotation.target().value().toUpperCase(Locale.ROOT));
        if (iInjectionTarget == null || iRedirectTarget == null) {
            throw new InvalidTargetException(transformerMethod, transformer, annotation.target().value(), this.redirectTargets.keySet());
        }

        for (String targetCombi : annotation.method()) {
            List<MethodNode> targets = ASMUtils.getMethodsFromCombi(transformedClass, targetCombi);
            if (targets.isEmpty()) throw new MethodNotFoundException(transformedClass, transformer, targetCombi);
            for (MethodNode target : targets) {
                if (Modifier.isStatic(target.access) != Modifier.isStatic(transformerMethod.access)) {
                    boolean isStatic = Modifier.isStatic(target.access);
                    throw new TransformerException(transformerMethod, transformer, "must " + (isStatic ? "" : "not ") + "be static")
                            .help(Codifier.of(transformerMethod).access(isStatic ? transformerMethod.access | Modifier.STATIC : transformerMethod.access & ~Modifier.STATIC));
                }

                List<AbstractInsnNode> injectionInstructions = iInjectionTarget.getTargets(injectionTargets, target, annotation.target(), annotation.slice());
                if (injectionInstructions == null) {
                    throw new TransformerException(transformerMethod, transformer, "has invalid member declaration '" + annotation.target().target() + "'")
                            .help("e.g. Ljava/lang/String;toString()V, Ljava/lang/Integer;MAX_VALUE:I");
                }
                if (injectionInstructions.isEmpty() && !annotation.target().optional()) {
                    throw new TransformerException(transformerMethod, transformer, "target '" + annotation.target().target() + "' could not be found")
                            .help("e.g. Ljava/lang/String;toString()V, Ljava/lang/Integer;MAX_VALUE:I");
                }

                this.renameAndCopy(transformerMethod, target, transformer, transformedClass, "CRedirect");
                iRedirectTarget.inject(transformedClass, target, transformer, transformerMethod, injectionInstructions);
            }
        }
    }

}
