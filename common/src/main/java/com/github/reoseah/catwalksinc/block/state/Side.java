package com.github.reoseah.catwalksinc.block.state;

import java.util.Locale;

public enum Side {
	LEFT, RIGHT;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ROOT);
	}

	public Side getOpposite() {
		return this == LEFT ? RIGHT : LEFT;
	}
}