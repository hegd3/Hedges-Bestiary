package com.hedge.hedges_bestiary.entity.ai.targeting;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class HBHurtByTargetGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private int timestamp;
    private int changeTargetCD;
    private final boolean alertAllies;
    private final Class<? extends Mob> toWarn;
    public HBHurtByTargetGoal(Mob pMob) {
        this(pMob, true, pMob.getClass());
    }

    public HBHurtByTargetGoal(Mob pMob, boolean alertAllies, Class<? extends Mob> toWarn) {
        super(pMob, true);
        this.alertAllies = alertAllies;
        this.toWarn = toWarn;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if (i != this.timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }
    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        this.changeTargetCD = 60;
        if (this.alertAllies) {
            this.alertOthers();
        }
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.changeTargetCD-- == 0) {
            LivingEntity lastHurt = this.mob.getLastHurtByMob();
            if (lastHurt != null && lastHurt != this.targetMob) {
                this.mob.setTarget(lastHurt);
                this.targetMob = this.mob.getTarget();
                this.timestamp = this.mob.getLastHurtByMobTimestamp();
            }
            this.changeTargetCD = 60;
        }
        return super.canContinueToUse();
    }

    protected void alertOthers() {
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(15, 6.0D, 15);
        List<? extends Mob> list = this.mob.level().getEntitiesOfClass(this.toWarn, aabb, EntitySelector.NO_SPECTATORS);
        for (Mob ally : list) {
            if (this.mob.isAlliedTo(ally) && ally.getTarget() == null && !ally.isAlliedTo(this.targetMob)) {
                ally.setTarget(this.targetMob);
            }
        }
    }
}
