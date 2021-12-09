package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CatwalkBlockEntity extends BlockEntity {
	protected final Map<Direction, @Nullable Handrail> handrails = new EnumMap<>(Direction.class);

	public CatwalkBlockEntity(BlockPos pos, BlockState state) {
		super(CIBlocks.BlockEntityTypes.CATWALK, pos, state);
	}

	public BlockState useWrench(Direction side, BlockState state, PlayerEntity player) {
		this.markDirty();
		if (!this.handrails.containsKey(side)) {
			this.handrails.put(side, Handrail.ALWAYS);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_handrail"), true);
			return state.cycle(CatwalkBlock.getHandrailProperty(side));
		} else if (this.handrails.get(side) == Handrail.ALWAYS) {
			this.handrails.put(side, Handrail.NEVER);
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_no_handrail"), true);
			return state.cycle(CatwalkBlock.getHandrailProperty(side));
		} else {
			this.handrails.remove(side);
			player.sendMessage(new TranslatableText("misc.catwalksinc.default_handrail"), true);
			return state.with(CatwalkBlock.getHandrailProperty(side),
					((CatwalkBlock) state.getBlock()).shouldHaveHandrail(this.world, this.pos, side));
		}
	}

	public Optional<Handrail> getHandrailState(Direction side) {
		return Optional.ofNullable(this.handrails.get(side));
	}

	public boolean isHandrailForced(Direction side) {
		return this.handrails.get(side) == Handrail.ALWAYS;
	}

	public boolean canBeRemoved() {
		return this.handrails.isEmpty();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtCompound nbtEnforced = nbt.getCompound("Enforced");
		for (Direction side : Direction.Type.HORIZONTAL) {
			if (nbtEnforced.contains(side.toString())) {
				this.handrails.put(side, Handrail.from(nbtEnforced.get(side.toString())));
			} else {
				this.handrails.remove(side);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtCompound nbtEnforced = new NbtCompound();
		for (Map.Entry<Direction, Handrail> entry : this.handrails.entrySet()) {
			nbtEnforced.put(entry.getKey().toString(), entry.getValue().toTag());
		}
		nbt.put("Enforced", nbtEnforced);
	}

	public enum Handrail {
		ALWAYS, NEVER;

		public NbtElement toTag() {
			return this == ALWAYS ? NbtByte.ONE : NbtByte.ZERO;
		}

		public static Handrail from(NbtElement nbt) {
			return NbtByte.ONE.equals(nbt) ? ALWAYS : NEVER;
		}
	}
}