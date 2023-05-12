package io.runebox.mixin.targets.impl;

import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.targets.IInjectionTarget;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A target for the given opcode.<br>
 * The opcode can be specified as a number or as its name.<br>
 * Invalid opcodes will be ignored.<br>
 * e.g. {@code INVOKESTATIC} or {@code 184}
 */
public class OpcodeTarget implements IInjectionTarget {

    @Override
    public List<AbstractInsnNode> getTargets(Map<String, IInjectionTarget> injectionTargets, MethodNode method, CTarget target, CSlice slice) {
        List<AbstractInsnNode> targets = new ArrayList<>();
        int opcode = this.getOpcode(target.target());
        if (opcode == -1) return null;

        int i = 0;
        for (AbstractInsnNode instruction : this.getSlice(injectionTargets, method, slice)) {
            if (instruction.getOpcode() != opcode) continue;
            if (target.ordinal() == -1 || target.ordinal() == i) targets.add(instruction);
            i++;
        }
        return targets;
    }

    private int getOpcode(final String opcode) {
        try {
            return Integer.parseInt(opcode);
        } catch (Throwable ignored) {
        }
        try {
            return Opcodes.class.getDeclaredField(opcode.toUpperCase(Locale.ROOT)).getInt(null);
        } catch (Throwable ignored) {
        }
        return -1;
    }

}
