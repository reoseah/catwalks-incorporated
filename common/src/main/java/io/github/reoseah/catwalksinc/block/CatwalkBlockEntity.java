package io.github.reoseah.catwalksinc.block;

import io.github.reoseah.catwalksinc.CatwalksInc;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CatwalkBlockEntity extends BlockEntity {
    protected CatwalkSideState south = CatwalkSideState.DEFAULT;
    protected CatwalkSideState west = CatwalkSideState.DEFAULT;
    protected CatwalkSideState north = CatwalkSideState.DEFAULT;
    protected CatwalkSideState east = CatwalkSideState.DEFAULT;

    public CatwalkBlockEntity(BlockPos pos, BlockState state) {
        super(CatwalksInc.CATWALK_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("south", this.south.name());
        nbt.putString("west", this.west.name());
        nbt.putString("north", this.north.name());
        nbt.putString("east", this.east.name());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.south = CatwalkSideState.valueOrDefault(nbt.getString("south"));
        this.west = CatwalkSideState.valueOrDefault(nbt.getString("west"));
        this.north = CatwalkSideState.valueOrDefault(nbt.getString("north"));
        this.east = CatwalkSideState.valueOrDefault(nbt.getString("east"));
    }

    public boolean isBlockEntityNecessary() {
        return this.south != CatwalkSideState.DEFAULT //
                || this.west != CatwalkSideState.DEFAULT //
                || this.north != CatwalkSideState.DEFAULT //
                || this.east != CatwalkSideState.DEFAULT;
    }

    public boolean canBeConnected(Direction side) {
        return switch (side) {
            case SOUTH -> this.south != CatwalkSideState.FORCE_HANDRAIL;
            case WEST -> this.west != CatwalkSideState.FORCE_HANDRAIL;
            case NORTH -> this.north != CatwalkSideState.FORCE_HANDRAIL;
            case EAST -> this.east != CatwalkSideState.FORCE_HANDRAIL;
            default -> false;
        };
    }

    public CatwalkSideState getSideState(Direction side) {
        return switch (side) {
            case SOUTH -> this.south;
            case WEST -> this.west;
            case NORTH -> this.north;
            case EAST -> this.east;
            default -> CatwalkSideState.DEFAULT;
        };
    }

    public void setSideState(Direction side, CatwalkSideState connectivity) {
        switch (side) {
            case SOUTH -> this.south = connectivity;
            case WEST -> this.west = connectivity;
            case NORTH -> this.north = connectivity;
            case EAST -> this.east = connectivity;
        }
        this.markDirty();
    }
}
