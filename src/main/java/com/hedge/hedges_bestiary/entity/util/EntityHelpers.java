package com.hedge.hedges_bestiary.entity.util;

import com.hedge.hedges_bestiary.util.WorldHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class EntityHelpers {

    public static final Vec3 UP = new Vec3(0, 1, 0);


    public static double angleFromEntity(Entity entity, Entity other) {
        Vec3 forward = Vec3.directionFromRotation(0, entity.getYRot()).normalize();
        Vec3 toOther = other.position().subtract(entity.position()).normalize();
        double dot = forward.dot(toOther);

        return dot;
    }

    public static boolean rightOfEntity(Entity entity, Entity other) {
        Vec3 forward = Vec3.directionFromRotation(0, entity.getYRot()).normalize();
        Vec3 toOther = other.position().subtract(entity.position()).normalize();

        double crossZ = forward.x * toOther.z - forward.z * toOther.x;

        return crossZ < 0;
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> ((ServerLevel) level).sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
    }

    public static double getRandomScaled(RandomSource random, double sc) {
        return (2.0D * random.nextFloat() - 1.0D) * sc;
    }

    public static Vec3 getRandomVec3(RandomSource random, double sc) {
        return new Vec3(getRandomScaled(random, sc), getRandomScaled(random, sc), getRandomScaled(random, sc));
    }


    public static void knockUp(LivingEntity hit, double vKB) {
        vKB = Math.max(vKB - hit.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue(), 0);
        hit.setDeltaMovement(hit.getDeltaMovement().add(0.0, vKB, 0.0));

    }

    public static Vec3 bodyAngle(LivingEntity entity) {
        return Vec3.directionFromRotation(0.0f, entity.yBodyRot);
    }

    public static Vec3 bodyAngle(LivingEntity entity, float xRot) {
        return Vec3.directionFromRotation(xRot, entity.yBodyRot);
    }

    @Nullable
    public static Vec3 getRandomSwimPos(PathfinderMob mob, int radius, int verticalDistance, boolean preferSurface) {
        Level level = mob.level();
        RandomSource random = mob.getRandom();
        Vec3 candidate = mob.position().add(radius * random.nextFloat() - radius * random.nextFloat(), 0, radius * random.nextFloat() - radius * random.nextFloat());
        BlockPos pos = WorldHelpers.fromVec3(candidate);
        if (preferSurface) {
            for (int i = 0; i < verticalDistance; i++) {
                if (level.getFluidState(pos.above()).is(FluidTags.WATER)) {
                    pos = pos.above();
                } else {
                    pos = pos.below();
                    break;
                }
            }
        } else {
            for (int i = 0; i < verticalDistance; i++) {
                if (level.getFluidState(pos.below()).is(FluidTags.WATER)) {
                    pos = pos.below();
                } else {
                    pos = pos.above();
                    break;
                }
            }
        }
        if (!level.getFluidState(pos).is(FluidTags.WATER)) {
            return null;
        }

        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }


    public static boolean isWaterBlock(Level world, BlockPos pos) {
        return world.getFluidState(pos).is(FluidTags.WATER);
    }

    public static boolean closeToSurface(LivingEntity entity, int maxDist) {
        return isNearWaterBoundary(entity, maxDist, Direction.UP);
    }


    public static int blocksFromWaterBoundary(Level level, BlockPos basePos, int maxDistance, Direction direction) {


        if (!isWaterBlock(level, basePos) && !isWaterBlock(level, basePos.above())) {
            return 0;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int i = 1;
        while (i <= maxDistance) {
            mutablePos.set(basePos).move(direction, i);
            if (!isWaterBlock(level, mutablePos)) {
                return i;
            }
            i++;
        }

        return i;
    }

    public static int blocksFromWaterBoundary(LivingEntity entity, int maxDistance, Direction direction) {
        BlockPos basePos = entity.blockPosition();
        Level level = entity.level();

        if (!isWaterBlock(level, basePos) && !isWaterBlock(level, basePos.above())) {
            return 0;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int i = 1;
        while (i <= maxDistance) {
            mutablePos.set(basePos).move(direction, i);
            if (!isWaterBlock(level, mutablePos)) {
                return i;
            }
            i++;
        }

        return i;
    }

    public static boolean isNearWaterBoundary(LivingEntity entity, int maxDistance, Direction direction) {
        BlockPos basePos = entity.blockPosition();
        Level level = entity.level();

        if (!isWaterBlock(level, basePos) && !isWaterBlock(level, basePos.above())) {
            return false;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= maxDistance; i++) {
            mutablePos.set(basePos).move(direction, i);
            if (!isWaterBlock(level, mutablePos)) {
                return true;
            }
        }

        return false;
    }




}
