package io.runebox.mixin.targets.impl;

import io.runebox.mixin.InjectionCallback;
import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.annotation.injection.CInject;
import io.runebox.mixin.targets.IInjectionTarget;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A target for {@link Opcodes#ATHROW} instructions.<br>
 * When using {@link CInject} the original exception is accessible using the {@link InjectionCallback}.
 */
public class ThrowTarget implements IInjectionTarget {

    @Override
    public List<AbstractInsnNode> getTargets(Map<String, IInjectionTarget> injectionTargets, MethodNode method, CTarget target, CSlice slice) {
        List<AbstractInsnNode> targets = new ArrayList<>();
        int i = 0;
        for (AbstractInsnNode instruction : this.getSlice(injectionTargets, method, slice)) {
            if (instruction.getOpcode() == Opcodes.ATHROW) {
                if (target.ordinal() == -1 || target.ordinal() == i) targets.add(instruction);
                i++;
            }
        }
        return targets;
    }

    @Override
    public CTarget.Shift getShift(CTarget target) {
        return CTarget.Shift.BEFORE;
    }

}
