package io.runebox.mixin.targets.impl;

import io.runebox.mixin.annotation.CSlice;
import io.runebox.mixin.annotation.CTarget;
import io.runebox.mixin.targets.IInjectionTarget;
import io.runebox.mixin.utils.ASMUtils;
import io.runebox.mixin.utils.MemberDeclaration;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A target for method invocation instructions.<br>
 * e.g. {@code java/lang/System.currentTimeMillis()J}
 */
public class InvokeTarget implements IInjectionTarget {

    @Override
    public List<AbstractInsnNode> getTargets(Map<String, IInjectionTarget> injectionTargets, MethodNode method, CTarget target, CSlice slice) {
        List<AbstractInsnNode> targets = new ArrayList<>();
        MemberDeclaration memberDeclaration = ASMUtils.splitMemberDeclaration(target.target());
        if (memberDeclaration == null) return null;

        int i = 0;
        for (AbstractInsnNode instruction : this.getSlice(injectionTargets, method, slice)) {
            if (!(instruction instanceof MethodInsnNode)) continue;
            MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
            if (methodInsnNode.owner.equals(memberDeclaration.getOwner()) && methodInsnNode.name.equals(memberDeclaration.getName()) && methodInsnNode.desc.equals(memberDeclaration.getDesc())) {
                if (target.ordinal() == -1 || target.ordinal() == i) targets.add(instruction);
                i++;
            }
        }
        return targets;
    }

}
