package io.runebox.mixin.mappings.impl;

import io.runebox.mixin.MixinManager;
import io.runebox.mixin.mappings.AMapper;
import io.runebox.mixin.mappings.MapperConfig;

/**
 * Only used internally when no other {@link AMapper} has been specified in the {@link MixinManager}.<br>
 * This mapper does not remap anything but is still required to fill all annotation values with the correct values (e.g. resolving wildcard members).
 */
public class VoidMapper extends AMapper {

    public VoidMapper() {
        super(MapperConfig.create());
    }

    @Override
    protected void init() {
    }

}
