package com.hedge.hedges_bestiary.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

public class WorldHelpers {

    public static BlockPos fromCoords(double x, double y, double z){
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public static BlockPos fromVec3(Vec3 vec3){
        return fromCoords(vec3.x, vec3.y, vec3.z);
    }

    public static int getHighestY(Structure.GenerationContext context, int x, int z) {
        int highestY = context.heightAccessor().getMinBuildHeight();

        for (int dx = 0; dx < 11; dx++) {
            for (int dz = 0; dz < 11; dz++) {
                int y = context.chunkGenerator().getFirstOccupiedHeight(
                        x + dx,
                        z + dz,
                        Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(),
                        context.randomState()
                );

                highestY = Math.max(highestY, y);
            }
        }
        return highestY;
    }
}
