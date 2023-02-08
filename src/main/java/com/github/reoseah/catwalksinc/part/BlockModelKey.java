package com.github.reoseah.catwalksinc.part;

import alexiil.mc.lib.multipart.api.render.PartModelKey;
import net.minecraft.block.BlockState;

import java.util.Objects;

public class BlockModelKey extends PartModelKey {
    private final BlockState state;

    public BlockModelKey(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockModelKey that = (BlockModelKey) o;
        return this.state.equals(that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.state);
    }
}
