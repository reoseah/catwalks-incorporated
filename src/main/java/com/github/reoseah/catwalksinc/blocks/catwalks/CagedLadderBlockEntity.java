package com.github.reoseah.catwalksinc.blocks.catwalks;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.github.reoseah.catwalksinc.CIBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class CagedLadderBlockEntity extends BlockEntity {
	protected @Nullable ElementMode ladder;

	public CagedLadderBlockEntity(BlockPos pos, BlockState state) {
		super(CIBlocks.BlockEntityTypes.CAGED_LADDER, pos, state);
	}

	public BlockState useWrench(BlockState state, PlayerEntity player) {
		this.markDirty();
		if (this.ladder == null) {
			this.ladder = ElementMode.ALWAYS;
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_ladder"), true);
			return state.cycle(CagedLadderBlock.EXTENSION);
		} else if (this.ladder == ElementMode.ALWAYS) {
			this.ladder = ElementMode.NEVER;
			player.sendMessage(new TranslatableText("misc.catwalksinc.forced_no_ladder"), true);
			return state.cycle(CagedLadderBlock.EXTENSION);
		} else {
			this.ladder = null;
			player.sendMessage(new TranslatableText("misc.catwalksinc.default_ladder"), true);
			return state.with(CagedLadderBlock.EXTENSION,
					((CagedLadderBlock) state.getBlock()).shouldChangeToExtension(state, this.world, this.pos));
		}
	}

	public Optional<ElementMode> getLadderState() {
		return Optional.ofNullable(this.ladder);
	}

	public boolean isLadderForced() {
		return this.ladder == ElementMode.ALWAYS;
	}

	public boolean canBeRemoved() {
		return this.ladder == null;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("Ladder")) {
			this.ladder = ElementMode.from(nbt.get("Ladder"));
		} else {
			this.ladder = null;
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.ladder != null) {
			nbt.put("Ladder", this.ladder.toTag());
		}
	}
}
