package com.hedge.hedges_bestiary.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

public class WaterMobBucketItem extends MobBucketItem {
    public WaterMobBucketItem(Supplier<? extends EntityType<?>> pType) {
        super(pType.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1));
    }
}
