package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.CatwalksInc;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CatwalkStairsBlockEntity extends BlockEntity {
    protected CatwalkSideState left = CatwalkSideState.DEFAULT;
    protected CatwalkSideState right = CatwalkSideState.DEFAULT;

    public CatwalkStairsBlockEntity(BlockPos pos, BlockState state) {
        super(CatwalksInc.CATWALK_STAIRS_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("left", this.left.name());
        nbt.putString("right", this.right.name());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.left = CatwalkSideState.valueOrDefault(nbt.getString("left"));
        this.right = CatwalkSideState.valueOrDefault(nbt.getString("right"));
    }

    public boolean isBlockEntityNecessary() {
        return this.left != CatwalkSideState.DEFAULT || this.right != CatwalkSideState.DEFAULT;
    }

    public boolean canBeConnected(CatwalkStairsBlock.StairSide side) {
        return switch (side) {
            case LEFT -> this.left != CatwalkSideState.DISABLE_HANDRAIL;
            case RIGHT -> this.right != CatwalkSideState.DISABLE_HANDRAIL;
        };
    }

    public CatwalkSideState getSideState(CatwalkStairsBlock.StairSide side) {
        return switch (side) {
            case LEFT -> this.left;
            case RIGHT -> this.right;
        };
    }

    public void setSideState(CatwalkStairsBlock.StairSide side, CatwalkSideState connectivity) {
        switch (side) {
            case LEFT -> this.left = connectivity;
            case RIGHT -> this.right = connectivity;
        }
        this.markDirty();
    }
}
