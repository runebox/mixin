package io.runebox.mixin.mappings.impl;

import io.runebox.mixin.mappings.AMapper;
import io.runebox.mixin.mappings.MapperConfig;
import io.runebox.mixin.utils.mappings.MapRemapper;

/**
 * A remapper that uses the given {@link MapRemapper} for remapping.
 */
public class RawMapper extends AMapper {

    private final MapRemapper remapper;

    public RawMapper(final MapperConfig config, final MapRemapper remapper) {
        super(config);
        this.remapper = remapper;
    }

    @Override
    protected void init() throws Throwable {
        super.remapper.copy(this.remapper);
    }

}
