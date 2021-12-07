package com.github.reoseah.catwalksinc.blocks;

import java.util.EnumMap;
import java.util.Map;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CatwalkBlockEntity extends BlockEntity {
	// true - forced handrail at that side
	// false - forced no handrail
	// no entry - default behavior
	protected final Map<Direction, Boolean> enforced = new EnumMap<>(Direction.class);

	public CatwalkBlockEntity(BlockPos pos, BlockState state) {
		super(CIBlocks.BlockEntityTypes.CATWALK, pos, state);
	}

	public BlockState onWrenched(Direction side, BlockState state, PlayerEntity player) {
		if (!this.enforced.containsKey(side)) {
			this.enforced.put(side, true);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_handrail"), true);
			return state.with(CatwalkBlock.sideToProperty(side), true);
		} else if (this.enforced.get(side)) {
			this.enforced.put(side, false);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_no_handrail"), true);
			return state.with(CatwalkBlock.sideToProperty(side), false);
		} else {
			this.enforced.remove(side);
			player.sendMessage(new TranslatableText("misc.catwalksinc.default_handrail"), true);
			return state.with(CatwalkBlock.sideToProperty(side),
					((CatwalkBlock) state.getBlock()).shouldHaveHandrail(this.world, this.pos, side));
		}
	}

	public boolean canConnect(Direction side) {
		return this.enforced.getOrDefault(side, false) != true;
	}

	public boolean canBeRemoved() {
		return this.enforced.isEmpty();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtCompound nbtEnforced = nbt.getCompound("Enforced");
		for (Direction side : Direction.Type.HORIZONTAL) {
			if (nbtEnforced.contains(side.toString())) {
				this.enforced.put(side, nbtEnforced.getBoolean(side.toString()));
			} else {
				this.enforced.remove(side);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtCompound nbtEnforced = new NbtCompound();
		for (Map.Entry<Direction, Boolean> entry : this.enforced.entrySet()) {
			nbtEnforced.putBoolean(entry.getKey().toString(), entry.getValue());
		}
		nbt.put("Enforced", nbtEnforced);
	}
}