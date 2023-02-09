package com.github.reoseah.catwalksinc.block;

import java.util.Locale;

public enum HorizontalHalf {
    LEFT, RIGHT;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public HorizontalHalf getOpposite() {
        return this == LEFT ? RIGHT : LEFT;
    }
}
