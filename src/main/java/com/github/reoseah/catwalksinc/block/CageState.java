package com.github.reoseah.catwalksinc.block;

import net.minecraft.util.StringIdentifiable;

public enum CageState implements StringIdentifiable {
    NORMAL("normal"), NONE("none"), HANDRAILS("handrails");

    private final String name;

    CageState(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
