package com.github.reoseah.catwalksinc.block;

import com.github.reoseah.catwalksinc.item.WrenchItem;
import com.github.reoseah.catwalksinc.part.ConnectionOverride;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CatwalkStairsBlockEntity extends BlockEntity {
    public static final BlockEntityType<CatwalkStairsBlockEntity> TYPE = FabricBlockEntityTypeBuilder.create(CatwalkStairsBlockEntity::new, CatwalkStairsBlock.INSTANCE).build();

    protected final Map<HorizontalHalf, ConnectionOverride> overrides = new EnumMap<>(HorizontalHalf.class);

    public CatwalkStairsBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtCompound nbtEnforced = nbt.getCompound("Overrides");
        for (HorizontalHalf half : HorizontalHalf.values()) {
            if (nbtEnforced.contains(half.toString())) {
                this.overrides.put(half, ConnectionOverride.from(nbtEnforced.get(half.toString())));
            } else {
                this.overrides.remove(half);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound overridesNbt = new NbtCompound();
        for (Map.Entry<HorizontalHalf, ConnectionOverride> entry : this.overrides.entrySet()) {
            overridesNbt.put(entry.getKey().toString(), entry.getValue().toTag());
        }
        nbt.put("Overrides", overridesNbt);
    }

    public void useWrench(HorizontalHalf half, BlockState state, PlayerEntity player) {
        ConnectionOverride next = ConnectionOverride.cycle(this.overrides.get(half));
        if (next == null) {
            this.overrides.remove(half);
            player.sendMessage(ConnectionOverride.DEFAULT_TEXT, true);
        } else {
            this.overrides.put(half, next);
            player.sendMessage(next.text, true);
        }
        this.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), WrenchItem.USE_SOUND, SoundCategory.PLAYERS, 1.0f, 1.0f / (this.getWorld().getRandom().nextFloat() * 0.4f + 1.2f));
        this.markDirty();
    }

    public Optional<ConnectionOverride> getHandrailState(HorizontalHalf half) {
        return Optional.ofNullable(this.overrides.get(half));
    }

    public boolean isHandrailForced(HorizontalHalf half) {
        return this.overrides.get(half) == ConnectionOverride.FORCED;
    }

    public boolean canBeRemoved() {
        return this.overrides.isEmpty();
    }
}
