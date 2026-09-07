package com.hedge.hedges_bestiary.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hedge.hedges_bestiary.registry.HBEffects;
import com.hedge.hedges_bestiary.registry.HBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MurklevolenceItem extends Item {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public MurklevolenceItem(Properties pProperties) {
        super(pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -3.1F, AttributeModifier.Operation.ADDITION));

        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (living) -> {
            living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        pTarget.addEffect(new MobEffectInstance(HBEffects.VOLATILITY.get(), 40), pAttacker);
        if (pTarget.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(HBParticles.MURK_IMPACT.get(), pTarget.getX(), pTarget.getY(0.5), pTarget.getZ(), 0, 0, 0, 0, 0);
        }

        return true;
    }


    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if ((double)pState.getDestroySpeed(pLevel, pPos) != 0.0D) {
            pStack.hurtAndBreak(2, pEntityLiving, (living) -> {
                living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }
}
