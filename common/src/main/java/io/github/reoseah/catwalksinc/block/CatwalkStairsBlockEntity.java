package io.github.reoseah.catwalksinc.block;

import io.github.reoseah.catwalksinc.CatwalksInc;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CatwalkStairsBlockEntity extends BlockEntity {
    protected Connectivity left = Connectivity.DEFAULT;
    protected Connectivity right = Connectivity.DEFAULT;

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
        this.left = Connectivity.valueOrDefault(nbt.getString("left"));
        this.right = Connectivity.valueOrDefault(nbt.getString("right"));
    }

    public boolean isBlockEntityNecessary() {
        return this.left != Connectivity.DEFAULT || this.right != Connectivity.DEFAULT;
    }

    public boolean canBeConnected(CatwalkStairsBlock.StairSide side) {
        return switch (side) {
            case LEFT -> this.left != Connectivity.DISABLE_HANDRAIL;
            case RIGHT -> this.right != Connectivity.DISABLE_HANDRAIL;
        };
    }

    public Connectivity getConnectivity(CatwalkStairsBlock.StairSide side) {
        return switch (side) {
            case LEFT -> this.left;
            case RIGHT -> this.right;
        };
    }

    public void setConnectivity(CatwalkStairsBlock.StairSide side, Connectivity connectivity) {
        switch (side) {
            case LEFT -> this.left = connectivity;
            case RIGHT -> this.right = connectivity;
        }
        this.markDirty();
    }

    public enum Connectivity {
        DEFAULT,
        DISABLE_HANDRAIL,
        FORCE_HANDRAIL;

        public final String translationKey = "catwalksinc.connectivity." + this.name().toLowerCase();

        public Connectivity cycle() {
            return switch (this) {
                case DEFAULT -> DISABLE_HANDRAIL;
                case DISABLE_HANDRAIL -> FORCE_HANDRAIL;
                case FORCE_HANDRAIL -> DEFAULT;
            };
        }

        public static Connectivity valueOrDefault(String name) {
            return switch (name) {
                case "DISABLE_HANDRAIL" -> DISABLE_HANDRAIL;
                case "FORCE_HANDRAIL" -> FORCE_HANDRAIL;
                default -> DEFAULT;
            };
        }
    }
}
