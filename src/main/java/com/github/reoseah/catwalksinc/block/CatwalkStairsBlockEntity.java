package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.part.ConnectionOverride;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CatwalkStairsBlockEntity extends BlockEntity {
    public static final BlockEntityType<CatwalkStairsBlockEntity> TYPE = FabricBlockEntityTypeBuilder.create(CatwalkStairsBlockEntity::new, CatwalkStairsBlock.INSTANCE).build();

    protected final Map<Side, ConnectionOverride> overrides = new EnumMap<>(Side.class);

    public CatwalkStairsBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtCompound nbtEnforced = nbt.getCompound("Overrides");
        for (Side side : Side.values()) {
            if (nbtEnforced.contains(side.toString())) {
                this.overrides.put(side, ConnectionOverride.from(nbtEnforced.get(side.toString())));
            } else {
                this.overrides.remove(side);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound overridesNbt = new NbtCompound();
        for (Map.Entry<Side, ConnectionOverride> entry : this.overrides.entrySet()) {
            overridesNbt.put(entry.getKey().toString(), entry.getValue().toTag());
        }
        nbt.put("Overrides", overridesNbt);
    }

    public void useWrench(Side side, BlockState state, PlayerEntity player) {
        ConnectionOverride next = ConnectionOverride.cycle(this.overrides.get(side));
        if (next == null) {
            this.overrides.remove(side);
            player.sendMessage(ConnectionOverride.DEFAULT_TEXT, true);
        } else {
            this.overrides.put(side, next);
            player.sendMessage(next.text, true);
        }
        this.markDirty();
    }

    public Optional<ConnectionOverride> getHandrailState(Side side) {
        return Optional.ofNullable(this.overrides.get(side));
    }

    public boolean isHandrailForced(Side side) {
        return this.overrides.get(side) == ConnectionOverride.FORCED;
    }

    public boolean canBeRemoved() {
        return this.overrides.isEmpty();
    }
}
