package com.github.reoseah.catwalksinc;

import com.github.reoseah.catwalksinc.block.CatwalkStairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CatwalksUtil {
    public static CatwalkStairsBlock.StairSide getTargettedSide(BlockPos pos, Vec3d hitPos, Direction facing) {
        Direction.Axis perpendicular = facing.rotateYClockwise().getAxis();
        double relative = hitPos.getComponentAlongAxis(perpendicular) - pos.getComponentAlongAxis(perpendicular);

        CatwalkStairsBlock.StairSide half = facing.rotateYClockwise().getDirection() == Direction.AxisDirection.POSITIVE && relative > 0.5 //
                || facing.rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE && relative < 0.5 //
                ? CatwalkStairsBlock.StairSide.LEFT : CatwalkStairsBlock.StairSide.RIGHT;
        return half;
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

    public static Direction getTargetedQuarter(BlockPos pos, Vec3d point) {
        double dx = point.getX() - pos.getX();
        double dz = point.getZ() - pos.getZ();

        if (Math.abs(dx - 0.5) > Math.abs(dz - 0.5)) {
            if (dx > 0.5) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (dz > 0.5) {
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        }
    }
}
