package com.github.reoseah.catwalksinc.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;

public class WrenchHelper {
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

	public static Side getBlockHalf(BlockPos pos, Vec3d hitPos, Direction facing) {
		Axis perpendicular = facing.rotateYClockwise().getAxis();
		double relative = hitPos.getComponentAlongAxis(perpendicular) - pos.getComponentAlongAxis(perpendicular);
	
		Side stairsSide = facing.rotateYClockwise().getDirection() == AxisDirection.POSITIVE && relative > 0.5 //
				|| facing.rotateYClockwise().getDirection() == AxisDirection.NEGATIVE && relative < 0.5 //
						? Side.LEFT
						: Side.RIGHT;
		return stairsSide;
	}
}
