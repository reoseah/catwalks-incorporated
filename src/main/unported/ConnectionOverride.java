package com.github.reoseah.catwalksinc.part;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public enum ConnectionOverride {
    FORCED(Text.translatable("misc.catwalksinc.forced_handrail")), //
    DISABLED(Text.translatable("misc.catwalksinc.forced_no_handrail"));

    public final Text text;

    public static final Text DEFAULT_TEXT = Text.translatable("misc.catwalksinc.default_handrail");

    ConnectionOverride(Text text) {
        this.text = text;
    }

    public static @Nullable ConnectionOverride cycle(@Nullable ConnectionOverride value) {
        if (value == null) {
            return FORCED;
        } else if (value == FORCED) {
            return DISABLED;
        } else {
            return null;
        }
    }

    public NbtElement toTag() {
        return this == FORCED ? NbtByte.ONE : NbtByte.ZERO;
    }

    public static ConnectionOverride from(NbtElement nbt) {
        return NbtByte.ONE.equals(nbt) ? FORCED : DISABLED;
    }
}
