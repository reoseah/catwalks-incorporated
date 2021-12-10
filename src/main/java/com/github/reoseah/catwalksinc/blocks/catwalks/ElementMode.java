package com.github.reoseah.catwalksinc.blocks.catwalks;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

public enum ElementMode {
	ALWAYS, NEVER;

	public NbtElement toTag() {
		return this == ALWAYS ? NbtByte.ONE : NbtByte.ZERO;
	}

	public static ElementMode from(NbtElement nbt) {
		return NbtByte.ONE.equals(nbt) ? ALWAYS : NEVER;
	}
}