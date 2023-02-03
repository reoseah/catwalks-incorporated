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

    public static Direction compare(BlockPos origin, BlockPos target) {
        int dx = target.getX() - origin.getX();
        int dy = target.getY() - origin.getY();
        int dz = target.getZ() - origin.getZ();
        
        if (dx == -1) {
            return Direction.WEST;
        } else if (dx == 1) {
            return Direction.EAST;
        } else if (dz == -1) {
            return Direction.NORTH;
        } else if (dz == 1) {
            return Direction.SOUTH;
        } else if (dy == -1) {
            return Direction.DOWN;
        } else if (dy == 1) {
            return Direction.UP;
        }
        return null;
    }
}
