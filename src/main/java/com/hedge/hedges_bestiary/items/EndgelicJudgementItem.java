package com.hedge.hedges_bestiary.items;

import com.hedge.hedges_bestiary.client.HBSounds;
import com.hedge.hedges_bestiary.entity.projectile.EndgelBullet;
import com.hedge.hedges_bestiary.registry.HBEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class EndgelicJudgementItem extends ProjectileWeaponItem {

    public static final Predicate<ItemStack> AMMO = (p_43017_) -> p_43017_.is(Items.ENDER_EYE);
    public EndgelicJudgementItem(Properties pProperties) {
        super(pProperties);
    }

    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean flag = player.getAbilities().instabuild;
            ItemStack itemstack = player.getProjectile(pStack);

            int i = this.getUseDuration(pStack) - pTimeLeft;
            i = EventHooks.onArrowLoose(pStack, pLevel, player, i, !itemstack.isEmpty());
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {

                float f = getPowerForTime(i);
                if (f >= 0.1F) {
                    boolean flag1 = player.getAbilities().instabuild;
                    if (!pLevel.isClientSide) {
                        LivingEntity target = player.getLastHurtMob() != null ? player.getLastHurtMob() : player.getLastHurtByMob();
                        if (target == null) {
                            List<LivingEntity> list = pLevel.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10D));
                            for (LivingEntity entity : list) {
                                if (entity != player && !entity.isAlliedTo(player)) {
                                    target = entity;
                                    break;
                                }
                            }
                        }
                        if (f > 0.5F) {
                            for (int k = -5; k <= 5; k += 5) {
                                this.createProjectile(player, pLevel, target, player.getYRot() + k, pStack, f);
                            }
                        } else {
                            this.createProjectile(player, pLevel, target, player.getYRot(), pStack, f);
                        }
                    }

                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), HBSounds.ENDGEL_SHOOT.get(), SoundSource.PLAYERS, 5.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.getCooldowns().addCooldown(this, 40);
                }
            }
        }
    }

    private void createProjectile(Player player, Level level, LivingEntity target, float yRot, ItemStack stack, float f) {
        EndgelBullet abstractarrow = new EndgelBullet(HBEntities.ENDGEL_BULLET.get(), level);
        abstractarrow.moveTo(player.getEyePosition());
        abstractarrow.setTarget(target);
        abstractarrow.shootFromRotation(player, player.getXRot(), yRot, 0.0F, f * 3.0F, 0.0F);


        stack.hurtAndBreak(this.getDurabilityUse(stack), player, LivingEntity.getSlotForHand(player.getUsedItemHand()));


        level.addFreshEntity(abstractarrow);
    }



    /**
     * Gets the velocity of the arrow entity from the bow's charge
     */
    public static float getPowerForTime(int pCharge) {
        float f = (float)pCharge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    /**
     * Returns the action that specifies what animation to play when the item is being used.
     */
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }


    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean flag = !pPlayer.getProjectile(itemstack).isEmpty();

        InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, pLevel, pPlayer, pHand, flag);
        if (ret != null) return ret;

        if (!pPlayer.getAbilities().instabuild && !flag) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }


    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return AMMO;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {
        projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot() + v2, 0.0F, v * 3.0F, 0.0F);


    }
}
