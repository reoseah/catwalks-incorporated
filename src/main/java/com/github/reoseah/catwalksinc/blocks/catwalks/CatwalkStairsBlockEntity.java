package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import com.github.reoseah.catwalksinc.CIBlocks;
import com.github.reoseah.catwalksinc.util.Side;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class CatwalkStairsBlockEntity extends BlockEntity {
	protected final Map<Side, ElementMode> handrails = new EnumMap<>(Side.class);

	public CatwalkStairsBlockEntity(BlockPos pos, BlockState state) {
		super(CIBlocks.BlockEntityTypes.CATWALK_STAIRS, pos, state);
	}

	public BlockState useWrench(Side side, BlockState state, PlayerEntity player) {
		this.markDirty();
		if (!this.handrails.containsKey(side)) {
			this.handrails.put(side, ElementMode.ALWAYS);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_handrail"), true);
			return state.with(CatwalkStairsBlock.getHandrailProperty(side), true);
		} else if (this.handrails.get(side) == ElementMode.ALWAYS) {
			this.handrails.put(side, ElementMode.NEVER);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_no_handrail"), true);
			return state.with(CatwalkStairsBlock.getHandrailProperty(side), false);
		} else {
			this.handrails.remove(side);
			player.sendMessage(new TranslatableText("misc.catwalksinc.default_handrail"), true);
			return state.with(CatwalkStairsBlock.getHandrailProperty(side),
					((CatwalkStairsBlock) state.getBlock()).shouldHaveHandrail(state, this.world, this.pos,
							CatwalkStairsBlock.getSideDirection(state.get(CatwalkStairsBlock.FACING), side), null));
		}
	}

	public Optional<ElementMode> getHandrailState(Side side) {
		return Optional.ofNullable(this.handrails.get(side));
	}

	public boolean isHandrailForced(Side side) {
		return this.handrails.get(side) == ElementMode.ALWAYS;
	}

	public boolean canBeRemoved() {
		return this.handrails.isEmpty();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtCompound nbtEnforced = nbt.getCompound("Enforced");
		for (Side side : Side.values()) {
			if (nbtEnforced.contains(side.toString())) {
				this.handrails.put(side, ElementMode.from(nbtEnforced.get(side.toString())));
			} else {
				this.handrails.remove(side);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtCompound nbtEnforced = new NbtCompound();
		for (Map.Entry<Side, ElementMode> entry : this.handrails.entrySet()) {
			nbtEnforced.put(entry.getKey().toString(), entry.getValue().toTag());
		}
		nbt.put("Enforced", nbtEnforced);
	}
}