package com.hedge.hedges_bestiary.entity.ai.goal.specific;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.ai.control.AdvancedTurner;
import com.hedge.hedges_bestiary.entity.ai.goal.GenericMeleeGoal;
import com.hedge.hedges_bestiary.entity.living.MurkEntity;
import com.hedge.hedges_bestiary.entity.living.ambientfish.SkibEntity;
import com.hedge.hedges_bestiary.entity.util.EntityHelpers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MurkAttackGoal extends GenericMeleeGoal<MurkEntity> {

    private boolean shouldPathAway;
    private int pathTicks;


    public MurkAttackGoal(MurkEntity entity) {
        super(entity, 1.6);
    }


    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && super.canUse();
    }

    @Override
    public void start() {
        super.start();
        this.shouldPathAway = false;
        this.pathTicks = 0;
    }

    @Override
    protected void tickPath(LivingEntity livingentity) {

        if (this.mob.getAnimState() < 2) {
            this.attackReach = this.mob.getAttackReachSqr(livingentity);
            this.dist = this.mob.distanceToSqr(livingentity);

            if (!(livingentity instanceof SkibEntity) && this.mob.isInFluidType() && (this.canStartPathAway() || this.shouldPathAway)) {
                this.pathTicks++;
                if (!this.shouldPathAway) {
                    Vec3 vec3 = EntityHelpers.getRandomSwimPos(this.mob, 10, 5, true);
                    if (vec3 != null) {
                        Path path = this.mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                        if (path != null) {
                            this.shouldPathAway = true;
                            this.mob.getNavigation().moveTo(path, this.getSpeedModifier());
                        }
                    }
                }

            } else if (this.attackReach < this.dist) {
                super.tickPath(livingentity);
            } else {
                //this.mob.lookAt(livingentity, 30f, 30f);
                this.mob.getLookControl().setLookAt(livingentity, 30f, 30f);
            }
        }
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.tickPath(livingentity);
        } else {
            return;
        }

        if (this.mob.getAnimState() == 0 && this.mob.hasLineOfSight(livingentity)) {
            if (livingentity instanceof SkibEntity) {
                if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                    this.mob.setAttacking();
                }
            } else {
                if (this.mob.isInFluidType()) {
                    if (this.mob.canMultiBite(this.attackReach, this.dist)) {
                        this.mob.setAnimState(3);
                        this.mob.getNavigation().stop();
                        this.mob.setTurnType(AdvancedTurner.TurnType.WHOLE_BODY);
                    } else if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                        if (this.mob.getRandom().nextInt(5) == 0) {
                            this.mob.setSlam();
                            this.mob.getNavigation().stop();
                            this.mob.setTurnType(AdvancedTurner.TurnType.WHOLE_BODY);
                        } else {
                            this.mob.setAttacking();
                        }
                    } else if (this.mob.getNavigation().isDone() || this.pathTicks > 40) {
                        if (this.mob.canRoar(this.attackReach, this.dist)) {
                            this.mob.setAnimState(4);
                            this.mob.playSound(HBSounds.MURK_ROAR.get(), 1.5F, 1F);
                            this.mob.getNavigation().stop();
                            this.mob.setTurnType(AdvancedTurner.TurnType.WHOLE_BODY);
                        } else if (this.mob.getProjCD() == 0) {
                            this.mob.setShooting();
                            this.mob.getNavigation().stop();
                        }
                        this.pathTicks = 0;
                        this.shouldPathAway = false;
                    }
                } else if (this.mob.canRoar(this.attackReach, this.dist)) {
                    this.mob.setAnimState(4);
                    this.mob.playSound(HBSounds.MURK_ROAR.get(), 1.5F, 1F);
                    this.mob.getNavigation().stop();
                    this.mob.setTurnType(AdvancedTurner.TurnType.WHOLE_BODY);
                } else if (this.mob.canMultiBite(this.attackReach, this.dist)) {
                    this.mob.setAnimState(3);
                    this.mob.getNavigation().stop();
                    this.mob.setTurnType(AdvancedTurner.TurnType.WHOLE_BODY);
                } else if (this.mob.canUseAttack(livingentity, this.attackReach, this.dist)) {
                    this.mob.setAttacking();
                } else if (this.mob.canShoot(this.attackReach, this.dist)) {
                    this.mob.setShooting();
                    this.mob.getNavigation().stop();
                }
            }
        }
    }

    private boolean canStartPathAway() {
        return this.mob.canRoar(this.attackReach, this.dist) || this.mob.canShoot(this.attackReach, this.dist);
    }

    @Override
    protected double getSpeedModifier() {
        if (this.mob.getAnimState() > 0) return 1.1f;
        return super.getSpeedModifier();
    }
}
