package com.hedge.hedges_bestiary.entity.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AttackHelpers {


    public static List<LivingEntity> zoneHitbox(LivingEntity entity, Vec3 offset, double pX, double pY, double pZ, int limit) {

        Vec3 origin = entity.position().add(offset);

        AABB zone = new AABB(origin.subtract(pX, pY, pZ), origin.add(pX, pY, pZ));

        List<LivingEntity> hit = new ArrayList<>();

        for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class, zone)) {
            if (target != entity && target.isAlive() && !entity.isAlliedTo(target) && entity.hasLineOfSight(target)) {
                hit.add(target);
                if (hit.size() >= limit) {
                    return hit;
                }
            }
        }

        return hit;

    }

    public static List<LivingEntity> zoneHitbox(Level level, Vec3 origin, double pX, double pY, double pZ, int limit) {


        AABB zone = new AABB(origin.subtract(pX, pY, pZ), origin.add(pX, pY, pZ));

        List<LivingEntity> hit = new ArrayList<>();

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, zone)) {
            if (target.isAlive()) {
                hit.add(target);
                if (hit.size() >= limit) {
                    return hit;
                }
            }
        }

        return hit;

    }

    public static List<LivingEntity> zoneHitbox(LivingEntity entity, Vec3 offset, double sideOffset, double pX, double pY, double pZ, int limit) {

        Vec3 sideVec = offset.cross(EntityHelpers.UP).normalize();
        Vec3 origin = entity.position().add(offset).add(sideVec.scale(sideOffset));

        AABB zone = new AABB(origin.subtract(pX, pY, pZ), origin.add(pX, pY, pZ));

        List<LivingEntity> hit = new ArrayList<>();

        for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class, zone)) {
            if (target != entity && target.isAlive() && !entity.isAlliedTo(target) && entity.hasLineOfSight(target)) {
                hit.add(target);
                if (hit.size() >= limit) {
                    return hit;
                }
            }
        }

        return hit;

    }

    public static List<LivingEntity> projectileZoneHitbox(Entity entity, Vec3 origin, double pX, double pY, double pZ, int limit) {

        AABB zone = new AABB(origin.subtract(pX, pY, pZ), origin.add(pX, pY, pZ));

        List<LivingEntity> hit = new ArrayList<>();

        for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class, zone)) {
            if (target != entity && target.isAlive() && !entity.isAlliedTo(target)) {
                hit.add(target);
                if (hit.size() >= limit) {
                    return hit;
                }
            }
        }

        return hit;

    }

    public static boolean singleTargetHitbox(LivingEntity entity, LivingEntity target, Vec3 offset, double pX, double pY, double pZ) {

        Vec3 origin = entity.position().add(offset);

        AABB zone = new AABB(origin.subtract(pX, pY, pZ), origin.add(pX, pY, pZ));


        for (LivingEntity e : entity.level().getEntitiesOfClass(LivingEntity.class, zone)) {
            if (e == target && entity.hasLineOfSight(target)) {
                return true;
            }
        }

        return false;

    }

    public static boolean blockBreak(LivingEntity target) {
        if (target instanceof Player player && player.isBlocking()) {
            player.disableShield();
            return true;
        }
        return false;
    }

    public static boolean betterHurt(LivingEntity entity, LivingEntity target, float damageMultiplier) {
        return target.hurt(target.damageSources().mobAttack(entity), (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * damageMultiplier);
    }

    public static boolean betterHurt(LivingEntity entity, LivingEntity target, float damageMultiplier, float kbMultiplier) {
        if (betterHurt(entity, target, damageMultiplier)) {
            target.knockback(0.8D + 0.5D * kbMultiplier, entity.getX() - target.getX(), entity.getZ() - target.getZ());
            return true;
        }
        return false;
    }


}
