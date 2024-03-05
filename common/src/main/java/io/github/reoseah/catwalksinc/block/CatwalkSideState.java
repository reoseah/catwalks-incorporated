package io.github.reoseah.catwalksinc.block;

public enum CatwalkSideState {
    DEFAULT,
    DISABLE_HANDRAIL,
    FORCE_HANDRAIL;

    public final String translationKey = "catwalksinc.catwalk_side_state." + this.name().toLowerCase();

    public CatwalkSideState cycle() {
        return switch (this) {
            case DEFAULT -> DISABLE_HANDRAIL;
            case DISABLE_HANDRAIL -> FORCE_HANDRAIL;
            case FORCE_HANDRAIL -> DEFAULT;
        };
    }

    public static CatwalkSideState valueOrDefault(String name) {
        return switch (name) {
            case "DISABLE_HANDRAIL" -> DISABLE_HANDRAIL;
            case "FORCE_HANDRAIL" -> FORCE_HANDRAIL;
            default -> DEFAULT;
        };
    }
}
