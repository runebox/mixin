package io.runebox.mixin.targets.impl;

import io.runebox.mixin.InjectionCallback;
import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.annotation.injection.CInject;
import io.runebox.mixin.targets.IInjectionTarget;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A target for the tail (before last {@link Opcodes#RETURN}) of a method.<br>
 * When using {@link CInject} the original return value is accessible using the {@link InjectionCallback}.
 */
public class TailTarget implements IInjectionTarget {

    @Override
    public List<AbstractInsnNode> getTargets(Map<String, IInjectionTarget> injectionTargets, MethodNode method, CTarget target, CSlice slice) {
        for (int i = method.instructions.size() - 1; i >= 0; i--) {
            AbstractInsnNode instruction = method.instructions.get(i);
            if (instruction.getOpcode() >= Opcodes.IRETURN && instruction.getOpcode() <= Opcodes.RETURN) return Collections.singletonList(instruction);
        }
        return Collections.emptyList();
    }

    @Override
    public CTarget.Shift getShift(CTarget target) {
        return CTarget.Shift.BEFORE;
    }

}
