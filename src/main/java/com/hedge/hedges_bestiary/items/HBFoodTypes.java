package com.hedge.hedges_bestiary.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class HBFoodTypes {

    public static final FoodProperties RAW_URKMEAT = new FoodProperties.Builder().meat().nutrition(3)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200), 0.5f).build();

    public static final FoodProperties COOKED_URKMEAT = new FoodProperties.Builder().meat().nutrition(8).saturationMod(0.4F)
            .build();

    public static final FoodProperties SKIB = new FoodProperties.Builder().meat().nutrition(3).build();

}
