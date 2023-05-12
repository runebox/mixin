package io.runebox.mixin.targets.impl;

import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.targets.IInjectionTarget;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A target for {@link Opcodes#NEW} instructions.<br>
 * e.g. {@code java/lang/String}
 */
public class NewTarget implements IInjectionTarget {

    @Override
    public List<AbstractInsnNode> getTargets(Map<String, IInjectionTarget> injectionTargets, MethodNode method, CTarget target, CSlice slice) {
        List<AbstractInsnNode> targets = new ArrayList<>();
        int i = 0;
        for (AbstractInsnNode instruction : this.getSlice(injectionTargets, method, slice)) {
            if (instruction.getOpcode() != Opcodes.INVOKESPECIAL) continue;
            MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
            if (!methodInsnNode.owner.equals(target.target())) continue;
            if (target.ordinal() == -1 || target.ordinal() == i) targets.add(instruction);
            i++;
        }
        return targets;
    }

}
