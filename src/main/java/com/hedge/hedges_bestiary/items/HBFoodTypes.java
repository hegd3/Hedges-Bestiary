package com.hedge.hedges_bestiary.items;

import com.hedge.hedges_bestiary.registry.HBEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class HBFoodTypes {

    public static final FoodProperties RAW_URKMEAT = new FoodProperties.Builder().nutrition(3)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200), 0.5f).build();

    public static final FoodProperties COOKED_URKMEAT = new FoodProperties.Builder().nutrition(8)
            .build();

    public static final FoodProperties SKIB = new FoodProperties.Builder().nutrition(3)
            .effect(() -> new MobEffectInstance(HBEffects.VOLATILITY.get(), 10), 0.5f).build();

}
