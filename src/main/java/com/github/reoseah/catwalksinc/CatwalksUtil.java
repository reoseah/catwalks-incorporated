package com.github.reoseah.catwalksinc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public class CatwalksUtil {
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

    public static Side getTargettedSide(BlockPos pos, Vec3d hitPos, Direction facing) {
        Direction.Axis perpendicular = facing.rotateYClockwise().getAxis();
        double relative = hitPos.getComponentAlongAxis(perpendicular) - pos.getComponentAlongAxis(perpendicular);

        Side side = facing.rotateYClockwise().getDirection() == Direction.AxisDirection.POSITIVE && relative > 0.5 //
                || facing.rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE && relative < 0.5 //
                ? Side.LEFT : Side.RIGHT;
        return side;
    }
}
